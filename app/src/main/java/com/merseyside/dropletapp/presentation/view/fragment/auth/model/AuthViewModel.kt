package com.merseyside.dropletapp.presentation.view.fragment.auth.model

import android.content.Context
import android.os.Bundle
import androidx.databinding.ObservableField
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.domain.interactor.GetOAuthProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.SaveTokenInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.merseyLib.utils.Logger
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class AuthViewModel(
    private val router: Router,
    private val getOAuthProvidersUseCase: GetOAuthProvidersInteractor,
    private val saveTokenInteractor: SaveTokenInteractor
) : BaseDropletViewModel(router) {

    private var provider: OAuthProvider? = null

    val oAuthProviders = ObservableField<List<OAuthProvider>>()
    val providersHint = ObservableField(getString(R.string.choose_provider_hint))

    override fun dispose() {
        getOAuthProvidersUseCase.cancel()
        saveTokenInteractor.cancel()
    }

    override fun readFrom(bundle: Bundle) {}

    override fun updateLanguage(context: Context) {
        providersHint.set(context.getString(R.string.choose_provider_hint))
    }

    override fun writeTo(bundle: Bundle) {}

    fun getOAuthProviders() {
        getOAuthProvidersUseCase.execute(
            onComplete = {
                Logger.log(this, it)
                oAuthProviders.set(it)
            }, onError = {
                showErrorMsg(errorMsgCreator.createErrorMsg(it))
            }
        )
    }

    fun saveToken(token: String) {
        if (provider != null) {
            saveTokenInteractor.execute(
                params = SaveTokenInteractor.Params(token = token, providerId = provider!!.provider.getId()),
                onComplete = {
                    if (it) {
                        getOAuthProviders()

                        navigateToServerCreationScreen(provider!!.provider)
                    }
                }, onError = {
                    showErrorMsg(errorMsgCreator.createErrorMsg(it))
                }
            )
        } else {
            showErrorMsg("Provider is null")
        }
    }

    fun setProvider(provider: OAuthProvider) {
        this.provider = provider
    }

    fun navigateToServerCreationScreen(provider: Provider) {
        if (provider !is Provider.Custom) {
            router.navigateTo(Screens.AddDropletScreen(provider))
        } else {
            router.navigateTo(Screens.AddCustomDropletScreen())
        }
    }

    fun onFreeAccessClick() {
        router.navigateTo(Screens.FreeAccessScreen())
    }
}