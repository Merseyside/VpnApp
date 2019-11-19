package com.merseyside.dropletapp.presentation.view.fragment.token.model

import android.content.Context
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.BuildConfig
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.SaveTokenInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.utils.isKeyValid
import com.merseyside.dropletapp.utils.isNameValid
import com.merseyside.dropletapp.utils.isTokenValid
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class TokenViewModel(
    router: Router,
    private val getProvidersUseCase: GetProvidersInteractor,
    private val saveTokenUseCase: SaveTokenInteractor
) : BaseDropletViewModel(router) {

    val tokenObservableField = ObservableField<Token>()
    val tokenNameObservableField = ObservableField<String>()

    val tokenHintObservableField = ObservableField<String>()
    val tokenNameHintObservableField = ObservableField<String>()
    val providerHintObservableField = ObservableField<String>()
    val saveButtonObservableField = ObservableField<String>()

    private var currentProviderId = 0L

    val providerLiveData = MutableLiveData<List<Provider>>()

    init {
        if (BuildConfig.DEBUG) {
            tokenNameObservableField.set("Test token")
        }

        getServices()
    }

    override fun updateLanguage(context: Context) {
        tokenHintObservableField.set(context.getString(R.string.hint_token))
        tokenNameHintObservableField.set(context.getString(R.string.hint_token_name))
        providerHintObservableField.set(context.getString(R.string.hint_provider_summary))
        saveButtonObservableField.set(context.getString(R.string.save_token))
    }

    override fun dispose() {
        getProvidersUseCase.cancel()
        saveTokenUseCase.cancel()
    }

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    private fun getServices() {
        getProvidersUseCase.execute(
            onComplete = {
                providerLiveData.value = it
            },
            onError = {

            }
        )
    }

    fun saveToken(provider: Provider) {

        val isValid = isTokenValid(tokenObservableField.get())

        if (isValid && isNameValid(tokenNameObservableField.get())) {

            val token = tokenObservableField.get()

            saveTokenUseCase.execute(
                params = SaveTokenInteractor.Params(
                    token = token!!,
                    name = tokenNameObservableField.get()!!,
                    providerId = provider.getId()
                ),
                onComplete = {
                    if (it) {
                        showMsg(getString(R.string.complete_msg))
                    }
                },
                onError = {throwable ->
                    showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
                },
                showProgress = { showProgress("Checking the correctness of the token") },
                hideProgress = { hideProgress() }
            )
        } else {
            showErrorMsg(getString(R.string.fill_fields_msg))
        }
    }

    fun setProviderId(id: Long) {
        currentProviderId = id

        if (BuildConfig.DEBUG) {
            when(id) {
                Provider.CryptoServers().getId() -> {
                    tokenObservableField.set("sUaw6E2pJoK6ti9jn9FTrlOqXjCEXGkLgraW3wlIAYGuuvJDDZDdx1bVAHwk")
                }

                else -> {}
            }

        }
    }

    companion object {
        private const val TAG = "TokenViewModel"
    }
}