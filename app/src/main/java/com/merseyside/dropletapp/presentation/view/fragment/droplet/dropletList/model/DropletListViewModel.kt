package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.admin.merseylibrary.system.FileSystemHelper
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
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

    val noItemsHintObservableFields = ObservableField<String>()


    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getDropletsUseCase.cancel()
        deleteDropletUseCase.cancel()
        getOvpnFileUseCase.cancel()
    }

    init {
        loadServers()
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun loadServers() {
        launch {
            getDropletsUseCase.observe().collect(dropletObserver)
        }
    }

    private val dropletObserver = object : FlowCollector<List<Server>> {
        override suspend fun emit(value: List<Server>) {

            if (value.isEmpty()) {
                dropletsVisibility.set(false)
            } else {
                dropletsVisibility.set(true)
            }

            dropletLiveData.value = value
        }
    }

    fun navigateToAuthScreen() {
        router.navigateTo(Screens.AuthScreen())
    }

    fun navigateToDropletScreen(server: Server) {
        router.navigateTo(Screens.DropletScreen(server))
    }



    fun deleteServer(server: Server) {
        deleteDropletUseCase.execute(
            params = DeleteDropletInteractor.Params(server.providerId, server.id),
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

    fun onServerClick(server: Server) {
        navigateToDropletScreen(server)
    }

    fun prepareServer(server: Server) {
        createServerUseCase.execute(
            params = CreateServerInteractor.Params(
                dropletId = server.id,
                providerId = server.providerId,
                logCallback = object: ProviderRepositoryImpl.LogCallback {
                    override fun onLog(log: String) {
                        Handler(Looper.getMainLooper()).post {
                            showProgress(log)
                        }
                    }
                }),
            onComplete = {
                loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            hideProgress = { hideProgress() }
        )
    }

    fun onAddServerClick() {

//            showAlertDialog(
//                VpnApplication.getInstance(),
//                messageRes = R.string.add_server_without_vpn_message,
//                positiveButtonTextRes = R.string.add_server_positive,
//                negativeButtonTextRes = R.string.add_server_negative,
//                onPositiveClick = {
//                    onServerClick(currentServer!!)
//                    navigateToAuthScreen()
//                }
//            )
//        } else {
        navigateToAuthScreen()
//        }
    }
}