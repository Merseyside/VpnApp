package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.content.Context
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.base.BaseVpnViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.merseyLib.utils.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class DropletListViewModel(
    private val router: Router,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val deleteDropletUseCase: DeleteDropletInteractor
) : BaseVpnViewModel(router), CoroutineScope {
    override fun updateLanguage(context: Context) {
        noItemsHintObservableFields.set(context.getString(R.string.no_servers))
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    val dropletsVisibility = ObservableField(true)
    val dropletLiveData = MutableLiveData<List<Server>>()

    val noItemsHintObservableFields = ObservableField(getString(R.string.no_servers))


    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getDropletsUseCase.cancel()
        deleteDropletUseCase.cancel()
        job.cancel()
    }

    init {
        loadServers()
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun loadServers() {
        launch {
            getDropletsUseCase.observe(
                onEmit = { value ->
                    if (value.isEmpty()) {
                        dropletsVisibility.set(false)
                    } else {
                        dropletsVisibility.set(true)
                    }

                    dropletLiveData.value = value
                }
            )
        }
    }

    fun navigateToAuthScreen() {
        router.navigateTo(Screens.AuthScreen())
    }

    private fun navigateToDropletScreen(server: Server) {
        router.navigateTo(Screens.DropletScreen(server))
    }

    fun deleteServer(server: Server) {
        deleteDropletUseCase.execute(
            params = DeleteDropletInteractor.Params(server.providerId, server.id),
            onComplete = {
                showMsg(getString(R.string.complete_msg))
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            onPreExecute = { showProgress() },
            onPostExecute = { hideProgress() }
        )
    }

    fun onServerClick(server: Server) {
        if (server.environmentStatus != SshManager.Status.STARTING) {
            navigateToDropletScreen(server)
        }
    }
}