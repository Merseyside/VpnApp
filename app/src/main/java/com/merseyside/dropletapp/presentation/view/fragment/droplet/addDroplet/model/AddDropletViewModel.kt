package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.GetRegionsByTokenInteractor
import com.merseyside.dropletapp.domain.interactor.GetTokensByProviderIdInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.utils.isServerNameValid
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class AddDropletViewModel(
    router: Router,
    private val getProvidersUseCase: GetProvidersInteractor,
    private val getTokensByProviderIdUseCase: GetTokensByProviderIdInteractor,
    private val getRegionsByTokenUseCase: GetRegionsByTokenInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router) {

    val providerHintObservableField = ObservableField<String>()
    val keyHintObservableField = ObservableField<String>()
    val regionHintObservableField = ObservableField<String>()
    val nameHintObservableField = ObservableField<String>()
    val buttonTextObservableField = ObservableField<String>()

    override fun updateLanguage(context: Context) {
        providerHintObservableField.set(context.getString(R.string.hint_provider_summary))
        keyHintObservableField.set(context.getString(R.string.hint_token_summary))
        regionHintObservableField.set(context.getString(R.string.hint_region_summary))
        nameHintObservableField.set(context.getString(R.string.hint_server_name_summary))
        buttonTextObservableField.set(context.getString(R.string.create_server))
    }

    val providerLiveData = MutableLiveData<List<Provider>>()
    val tokenLiveData = MutableLiveData<List<TokenEntity>>()
    val regionLiveData = MutableLiveData<List<RegionPoint>>()

    private var currentProvider: Long = 0
    private var currentToken: Token = ""
    private var currentRegion: RegionPoint? = null

    val serverNameObservableField = ObservableField<String>()

    init {
        getProviders()
    }

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
        getProvidersUseCase.cancel()
        getTokensByProviderIdUseCase.cancel()
        getRegionsByTokenUseCase.cancel()
        createServerUseCase.cancel()
    }

    private fun getProviders() {
        getProvidersUseCase.execute(
            onComplete = {
                providerLiveData.value = it
            },
            onError = {

            }
        )
    }

    fun getTokens(providerId: Long) {
        currentProvider = providerId

        getTokensByProviderIdUseCase.execute(
            params = GetTokensByProviderIdInteractor.Params(providerId),
            onComplete = {
                tokenLiveData.value = it
            },

            onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            }
        )
    }

    fun getRegions(token: Token) {
        currentToken = token

        getRegionsByTokenUseCase.execute(
            params = GetRegionsByTokenInteractor.Params(token, currentProvider),
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

    fun createServer() {

        try {
            createServerUseCase.execute(
                params = CreateServerInteractor.Params(
                    token = currentToken,
                    providerId = currentProvider,
                    regionSlug = currentRegion?.slug ?: throw IllegalArgumentException(),
                    serverName = serverNameObservableField.get()
                        .also { if (!isServerNameValid(serverNameObservableField.get())) throw IllegalArgumentException()
                            .also {
                                showErrorMsg("Wrong server name")
                            }
                        }
                        ?: throw IllegalArgumentException()
                            .also {
                                showErrorMsg("Server name can not be empty")
                            },
                    logCallback = object: ProviderRepositoryImpl.LogCallback {
                        override fun onLog(log: String) {
                            Handler(Looper.getMainLooper()).post {
                                showProgress(log)
                            }
                        }
                    }
                ),
                onComplete = {
                    back()
                },
                onError = {throwable ->
                    showErrorMsg(errorMsgCreator.createErrorMsg(throwable), getString(R.string.retry), View.OnClickListener {
                        createServer()
                    })
                },
                showProgress = { showProgress(getString(R.string.setup_server_msg)) },
                hideProgress = { hideProgress() }
            )
        } catch(e: Exception) {

        }
    }
}