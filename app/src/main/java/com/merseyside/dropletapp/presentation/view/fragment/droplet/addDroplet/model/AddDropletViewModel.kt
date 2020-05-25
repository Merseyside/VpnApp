package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetRegionsByProviderInteractor
import com.merseyside.dropletapp.domain.interactor.GetTypedConfigNamesInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.utils.getLogByStatus
import com.merseyside.merseyLib.utils.ext.onChange
import com.merseyside.merseyLib.utils.mainThread
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class AddDropletViewModel(
    private val router: Router,
    private val getRegionsByProviderUseCase: GetRegionsByProviderInteractor,
    private val createServerUseCase: CreateServerInteractor,
    private val getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
) : BaseDropletViewModel(router) {

    val vpnTypeObservableField = ObservableField(getString(R.string.vpn_type))
    val regionHintObservableField = ObservableField(getString(R.string.hint_region_summary))
    val buttonTextObservableField = ObservableField(getString(R.string.create_server))

    val types = ObservableField<List<String>>()
    val selectedType = ObservableField<String>()

    val isShadowsocks = ObservableField(false)
    val isV2RayEnabled = ObservableField(false)

    private var provider: Provider? = null

    init {
        selectedType.onChange { _, value, _ ->
            isShadowsocks.set(value == TypedConfig.Shadowsocks.name)
        }
    }

    override fun updateLanguage(context: Context) {
        vpnTypeObservableField.set(context.getString(R.string.vpn_type))
        regionHintObservableField.set(context.getString(R.string.hint_region_summary))
        buttonTextObservableField.set(context.getString(R.string.create_server))
    }

    val regionLiveData = MutableLiveData<List<RegionPoint>>()

    private var currentRegion: RegionPoint? = null

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getRegionsByProviderUseCase.cancel()
        createServerUseCase.cancel()
        getTypedConfigNamesUseCase.cancel()
    }

    fun setProvider(provider: Provider) {
        this.provider = provider

        getRegions(provider)
        getTypedConfigs()
    }

    private fun getRegions(provider: Provider) {

        getRegionsByProviderUseCase.execute(
            params = GetRegionsByProviderInteractor.Params(provider.getId()),
            onComplete = {
                regionLiveData.value = it
            }, onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            onPreExecute = { showProgress(getString(R.string.loading_regions_msg)) },
            onPostExecute = { hideProgress() }
        )
    }

    private fun getTypedConfigs() {
        getTypedConfigNamesUseCase.execute(
            onComplete = {
                if (!it.isNullOrEmpty()) {
                    types.set(it)
                    selectedType.set(types.get()!!.first())
                }
            }
        )
    }

    fun setRegion(region: RegionPoint) {
        currentRegion = region
    }

    fun createServer(): Boolean {
        if (selectedType.get() != null) {
            try {
                createServerUseCase.execute(
                    params = CreateServerInteractor.Params(
                        providerId = provider!!.getId(),
                        regionSlug = currentRegion?.slug ?: throw IllegalArgumentException(),
                        typedConfig = selectedType.get(),
                        isV2RayEnabled = if (selectedType.get()!! == TypedConfig.Shadowsocks.name) isV2RayEnabled.get() else null,
                        logCallback = object : ProviderRepositoryImpl.LogCallback {
                            override fun onLog(log: ProviderRepositoryImpl.LogStatus) {
                                mainThread {
                                    showProgress(getLogByStatus(getLocaleContext(), log))
                                }
                            }
                        }
                    ),
                    onComplete = {
                        newRootDropletListScreen()
                    },
                    onError = { throwable ->
                        if (!isNavigationEnable) {
                            isNavigationEnable = true
                        }
                        showErrorMsg(
                            errorMsgCreator.createErrorMsg(throwable),
                            getString(R.string.retry)
                        ) { createServer() }
                    },
                    onPreExecute = {
                        isNavigationEnable = false
                        showProgress(getString(R.string.setup_server_msg))
                    },
                    onPostExecute = { hideProgress() }
                )
            } catch (e: Exception) {
                return false
            }

            return true
        }

        return false
    }

    private fun newRootDropletListScreen() {
        router.newRootScreen(Screens.DropletListScreen())
    }

    override fun onBackPressed(): Boolean {
        return isNavigationEnable
    }
}