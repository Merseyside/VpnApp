package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetOvpnFileInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.ssh.SshManager
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.UpstreamConfigParser
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class DropletListViewModel(
    private val router: Router,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val deleteDropletUseCase: DeleteDropletInteractor,
    private val getOvpnFileUseCase: GetOvpnFileInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router), CoroutineScope {
    override fun updateLanguage(context: Context) {

    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    val dropletsVisibility = ObservableField<Boolean>(true)
    val dropletLiveData = MutableLiveData<List<Server>>()
    val vpnProfileLiveData = MutableLiveData<VpnProfile>()

    var currentServer: Server? = null


    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
        getDropletsUseCase.cancel()
        deleteDropletUseCase.cancel()
        getOvpnFileUseCase.cancel()
    }

    @UseExperimental(InternalCoroutinesApi::class)
    fun loadServers() {
        launch {
            getDropletsUseCase.observe().collect(dropletObserver)
        }
    }

    private val dropletObserver = object : FlowCollector<List<Server>> {
        override suspend fun emit(value: List<Server>) {
            if (value.isEmpty()) {
                dropletsVisibility.set(false)
            }

            dropletLiveData.value = value
        }

    }

    fun navigateToAddDropletScreen() {
        router.navigateTo(Screens.AddDropletScreen())
    }

    private fun loadVpnProfile(body: String): VpnProfile? {
        return UpstreamConfigParser.parseConfig(VpnApplication.getInstance(), body)
    }

    private fun prepareVpn(body: String) {
        val vpnProfile: VpnProfile? = loadVpnProfile(body)
        if (vpnProfile != null) {
            vpnProfileLiveData.value = vpnProfile
        }
    }

    fun deleteServer(server: Server) {
        deleteDropletUseCase.execute(
            params = DeleteDropletInteractor.Params(server.token, server.providerId, server.id),
            onComplete = {
                showMsg(getString(R.string.complete_msg))
                loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            showProgress = { showProgress() },
            hideProgress = { hideProgress() }
        )
    }

    fun getOvpnFile(server: Server) {

        if (currentServer != null) {
            val copiedServer = currentServer!!.copy()
            copiedServer.connectStatus = false
            dropletLiveData.value = listOf(copiedServer)
        }

        if (currentServer != server) {
            currentServer = server.copy()

            getOvpnFileUseCase.execute(
                params = GetOvpnFileInteractor.Params(server.id, server.providerId),
                onComplete = {
                    loadServers()

                    prepareVpn(it)
                },
                onError = { throwable ->
                    showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
                },
                showProgress = {
                    if (server.environmentStatus == SshManager.Status.IN_PROCESS) {
                        showProgress(getString(R.string.receiving_access_msg))
                    }},
                hideProgress = { hideProgress() }
            )
        } else {
            currentServer = null
        }
    }

    fun prepareServer(server: Server) {
        createServerUseCase.execute(
            params = CreateServerInteractor.Params(dropletId = server.id, providerId = server.providerId),
            onComplete = {
                loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            showProgress = { showProgress(getString(R.string.setup_server_msg)) },
            hideProgress = { hideProgress() }
        )
    }

    fun setConnectionStatus(status: VpnStatus.ConnectionStatus) {
        if (currentServer != null) {

            when (status) {
                VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                    if (currentServer!!.connectStatus) return
                    currentServer!!.connectStatus = true
                }

                else -> {
                    if (!currentServer!!.connectStatus) return
                    currentServer!!.connectStatus = false
                }
            }

            dropletLiveData.value = listOf(currentServer!!)
        }
    }

    fun isConnected(): Boolean {
        return currentServer != null
    }


    companion object {
        private const val TAG = "DropletListViewModel"
    }
}