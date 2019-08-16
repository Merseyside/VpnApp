package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetOvpnFileInteractor
import com.merseyside.dropletapp.domain.interactor.GetServersInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class DropletListViewModel(
    private val router: Router,
    private val getServersUseCase: GetServersInteractor,
    private val deleteDropletUseCase: DeleteDropletInteractor,
    private val getOvpnFileUseCase: GetOvpnFileInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router) {

    val dropletsVisibility = ObservableField<Boolean>(true)

    val dropletLiveData = MutableLiveData<List<Server>>()


    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
        getServersUseCase.cancel()
        deleteDropletUseCase.cancel()
        getOvpnFileUseCase.cancel()
    }

    fun loadServers() {
        getServersUseCase.execute(
            onComplete = {
                if (it.isEmpty()) {
                    dropletsVisibility.set(false)
                }

                dropletLiveData.value = it
            },
            onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            }
        )
    }

    fun navigateToAddDropletScreen() {
        router.navigateTo(Screens.AddDropletScreen())
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

    fun getOvpnFile(dropletId: Long, providerId: Long) {
        getOvpnFileUseCase.execute(
            params = GetOvpnFileInteractor.Params(dropletId, providerId),
            onComplete = {
                showMsg(it)
            },
            onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))

                prepareServer(dropletId, providerId)
            },
            showProgress = { showProgress() },
            hideProgress = { hideProgress() }
        )
    }

    fun prepareServer(dropletId: Long, providerId: Long) {
        createServerUseCase.execute(
            params = CreateServerInteractor.Params(dropletId = dropletId, providerId = providerId),
            onComplete = {
                loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            showProgress = { showProgress() },
            hideProgress = { hideProgress() }
        )
    }


}