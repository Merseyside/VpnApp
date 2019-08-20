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
    val tokenNameObservableField = ObservableField<String>("Test token")

    val tokenHintObservableField = ObservableField<String>()
    val tokenNameHintObservableField = ObservableField<String>()
    val providerHintObservableField = ObservableField<String>()
    val saveButtonObservableField = ObservableField<String>()


    val providerLiveData = MutableLiveData<List<Provider>>()

    init {
        if (BuildConfig.DEBUG) {
            tokenObservableField.set("12b95eab5d9c089185068cd22cff4a59af1d3195c5e3390c212192c4bb802630")
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

        if (isTokenValid(tokenObservableField.get()) && isNameValid(tokenNameObservableField.get())) {
            saveTokenUseCase.execute(
                params = SaveTokenInteractor.Params(
                    token = tokenObservableField.get()!!,
                    name = tokenNameObservableField.get()!!,
                    providerId = provider.getId()
                ),
                onComplete = {
                    if (it) {
                        showMsg(getString(R.string.complete_msg))
                    } else {
                        //showErrorMsg(getString(R.string.unknown_error_msg))
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

    companion object {
        private const val TAG = "TokenViewModel"
    }
}