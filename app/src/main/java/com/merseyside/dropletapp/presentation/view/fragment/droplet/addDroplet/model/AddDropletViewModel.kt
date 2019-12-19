package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetRegionsByProviderInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class AddDropletViewModel(
    private val router: Router,
    private val getRegionsByProviderUseCase: GetRegionsByProviderInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router) {

    val regionHintObservableField = ObservableField<String>()
    val buttonTextObservableField = ObservableField<String>()

    private var provider: Provider? = null
    private var isNavigationEnable = true

    override fun updateLanguage(context: Context) {
        regionHintObservableField.set(context.getString(R.string.hint_region_summary))
        buttonTextObservableField.set(context.getString(R.string.create_server))
    }

    val regionLiveData = MutableLiveData<List<RegionPoint>>()

    private var currentRegion: RegionPoint? = null

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
        getRegionsByProviderUseCase.cancel()
        createServerUseCase.cancel()
    }

    fun setProvider(provider: Provider) {
        this.provider = provider

        getRegions(provider)
    }

    private fun getRegions(provider: Provider) {

        getRegionsByProviderUseCase.execute(
            params = GetRegionsByProviderInteractor.Params(provider.getId()),
            onComplete = {
                regionLiveData.value = it
            }, onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            showProgress = { showProgress(getString(R.string.loading_regions_msg)) },
            hideProgress = { hideProgress() }
        )
    }

    fun setRegion(region: RegionPoint) {
        currentRegion = region
    }

    fun createServer(): Boolean {

        try {
            createServerUseCase.execute(
                params = CreateServerInteractor.Params(
                    providerId = provider!!.getId(),
                    regionSlug = currentRegion?.slug ?: throw IllegalArgumentException(),
                    logCallback = object: ProviderRepositoryImpl.LogCallback {
                        override fun onLog(log: String) {
                            Handler(Looper.getMainLooper()).post {
                                showProgress(log)

                                if (log == "Server is valid") {
                                    isNavigationEnable = true
                                }
                            }
                        }
                    }
                ),
                onComplete = {
                    newRootDropletListScreen()
                },
                onError = {throwable ->
                    if (!isNavigationEnable) {
                        isNavigationEnable = true
                    }
                    showErrorMsg(errorMsgCreator.createErrorMsg(throwable), getString(R.string.retry), View.OnClickListener {
                        createServer()
                    })
                },
                showProgress = {
                    isNavigationEnable = false
                    showProgress(getString(R.string.setup_server_msg))
                },
                hideProgress = { hideProgress() }
            )
        } catch(e: Exception) {
            return false
        }

        return true
    }

    fun newRootDropletListScreen() {
        router.newRootScreen(Screens.DropletListScreen())
    }

    override fun onBackPressed(): Boolean {
        return isNavigationEnable
    }
}