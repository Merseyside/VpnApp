package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.admin.merseylibrary.system.FileSystemHelper
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
import java.io.File
import kotlin.coroutines.CoroutineContext

class DropletListViewModel(
    private val router: Router,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val deleteDropletUseCase: DeleteDropletInteractor,
    private val getOvpnFileUseCase: GetOvpnFileInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router), CoroutineScope {
    override fun updateLanguage(context: Context) {
        noItemsHintObservableFields.set(context.getString(R.string.no_servers))
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    val dropletsVisibility = ObservableField<Boolean>(true)
    val dropletLiveData = MutableLiveData<List<Server>>()
    val vpnProfileLiveData = MutableLiveData<VpnProfile>()

    val connectionLiveData = MutableLiveData<Server>()
    val ovpnFileLiveData = MutableLiveData<File>()

    val noItemsHintObservableFields = ObservableField<String>()

    var currentServer: Server? = null


    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

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

            Log.d(TAG, value.size.toString())

            if (value.isEmpty()) {
                dropletsVisibility.set(false)
            } else {
                dropletsVisibility.set(true)
            }

            if (currentServer != null) {
                val newValue = value.map {
                    if (it.id == currentServer!!.id) currentServer!!
                    else it
                }

                dropletLiveData.value = newValue
                return

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

    fun connectToServer(server: Server) {

        if (currentServer != null) {
            currentServer!!.connectStatus = false
            connectionLiveData.value = currentServer
        }

        if (currentServer != server) {
            currentServer = server

            getOvpnFileUseCase.execute(
                params = GetOvpnFileInteractor.Params(server.token, server.id, server.providerId),
                onComplete = {
                    Log.d(TAG, it)
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

    fun shareOvpnFile(server: Server) {

        getOvpnFileUseCase.execute(
            params = GetOvpnFileInteractor.Params(server.token, server.id, server.providerId),
            onComplete = {
                ovpnFileLiveData.value = FileSystemHelper.createTempFile(server.name, ".ovpn", it)
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

            connectionLiveData.value = currentServer!!
        }
    }

    fun isConnected(): Boolean {
        return currentServer != null
    }

    fun showConnectedServer(server: Server) {
        Log.d(TAG, server.toString())

        currentServer = server

        if (!dropletLiveData.value.isNullOrEmpty()) {
            connectionLiveData.value = currentServer
        }
    }


    companion object {
        private const val TAG = "DropletListViewModel"
    }
}