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
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.mvvmcleanarch.utils.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class DropletListViewModel(
    private val router: Router,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val deleteDropletUseCase: DeleteDropletInteractor
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
        Logger.log(this, "dispose")

        getDropletsUseCase.cancel()
        deleteDropletUseCase.cancel()
        job.cancel()
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

            Logger.log(this, "emit")

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
}