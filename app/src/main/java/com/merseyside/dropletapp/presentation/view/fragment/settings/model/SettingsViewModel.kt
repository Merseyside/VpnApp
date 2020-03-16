package com.merseyside.dropletapp.presentation.view.fragment.settings.model

import android.content.Context
import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.domain.interactor.DeleteTokenInteractor
import com.merseyside.dropletapp.domain.interactor.GetAllTokensInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class SettingsViewModel(
    router: Router,
    private val getAllTokensUseCase: GetAllTokensInteractor,
    private val deleteTokenUseCase: DeleteTokenInteractor
) : BaseDropletViewModel(router) {

    val tokenLiveData = MutableLiveData<List<TokenEntity>>()
    val tokensVisibility = ObservableField<Boolean>()

    val tokenHintObservableField = ObservableField<String>()

    override fun dispose() {
        getAllTokensUseCase.cancel()
        deleteTokenUseCase.cancel()
    }

    override fun readFrom(bundle: Bundle) {
    }

    override fun updateLanguage(context: Context) {
        tokenHintObservableField.set(context.getString(R.string.tokens))
    }

    override fun writeTo(bundle: Bundle) {
    }

    fun getAllTokens() {
        getAllTokensUseCase.execute(
            onComplete = {
                if (it.isNotEmpty()) {
                    tokensVisibility.set(true)
                } else {
                    tokensVisibility.set(false)
                }

                tokenLiveData.value = it
            },
            onError = {throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            }
        )
    }

    fun deleteToken(tokenEntity: TokenEntity) {
        showAlertDialog(
            titleRes = R.string.delete_token,
            messageRes = R.string.delete_token_msg,
            positiveButtonTextRes = R.string.delete_action,
            negativeButtonTextRes = R.string.error_dialog_negative,
            onPositiveClick = {
                deleteTokenUseCase.execute(
                    params = DeleteTokenInteractor.Params(tokenEntity.token),
                    onComplete = {
                        showMsg(getString(R.string.successfully_deleted))

                        getAllTokens()
                    },
                    onError = {throwable ->
                        showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
                    }
                )
            }
        )
    }

}