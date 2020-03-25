package com.merseyside.dropletapp.presentation.view.fragment.droplet.addCustomServer.model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.interactor.CreateCustomServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetTypedConfigNamesInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.utils.getLogByStatus
import com.merseyside.mvvmcleanarch.utils.ext.onChange
import com.merseyside.mvvmcleanarch.utils.isIpValid
import com.merseyside.mvvmcleanarch.utils.mainThread
import kotlinx.coroutines.cancel
import ru.terrakok.cicerone.Router

class AddCustomDropletViewModel(
    private val router: Router,
    private val createCustomServerUseCase: CreateCustomServerInteractor,
    private val getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
) : BaseDropletViewModel(router) {

    val isPrivateKey = ObservableField(false)
    val authFieldHint = ObservableField<String>()
    val authField = ObservableField<String>()
    val authError = ObservableField("")

    val userNameField = ObservableField("root")
    val userNameError = ObservableField("")

    val hostField = ObservableField<String>()
    val hostError = ObservableField("")

    val portField = ObservableField(PORT)
    val portError = ObservableField("")

    val types = ObservableField<List<String>>()
    val selectedType = ObservableField<String>()

    val isShadowsocks = ObservableField(false)
    val isV2RayEnabled = ObservableField(false)

    init {
        setupLocales()

        isPrivateKey.onChange { _, value, _ ->
            authFieldHint.set(getString(
                if (value == true) R.string.ssh_key_hint else R.string.password_hint )
            )
        }

        selectedType.onChange { _, value, _ ->
            isShadowsocks.set(value == TypedConfig.Shadowsocks.name)
        }

        getTypes()
    }

    override fun updateLanguage(context: Context) {
        super.updateLanguage(context)

        setupLocales()
    }

    private fun setupLocales() {
        authFieldHint.set(getString(R.string.password_hint))
    }

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        createCustomServerUseCase.cancel()
        getTypedConfigNamesUseCase.cancel()
    }

    private fun getTypes() {
        getTypedConfigNamesUseCase.execute(
            onComplete = {
                if (!it.isNullOrEmpty()) {
                    types.set(it)
                    selectedType.set(types.get()!!.first())
                }
            }
        )
    }

    fun createCustomServer() {

        if (!isFieldsValid()) {
            showErrorMsg(getString(R.string.fill_fields_msg))
            return
        }

        createCustomServerUseCase.execute(
            params = CreateCustomServerInteractor.Params(
                typeName = selectedType.get()!!,
                userName = userNameField.get()!!,
                host = hostField.get()!!,
                port = portField.get()!!.toInt(),
                password = if (isPrivateKey.get() == false) authField.get()!! else null,
                sshKey = if (isPrivateKey.get() == true) authField.get()!! else null,
                isV2RayEnabled = if (selectedType.get()!! == TypedConfig.Shadowsocks.name) isV2RayEnabled.get() else null,
                logCallback = object : ProviderRepositoryImpl.LogCallback {
                    override fun onLog(log: ProviderRepositoryImpl.LogStatus) {
                        mainThread { showProgress(getLogByStatus(getLocaleContext(), log)) }
                    }
                }
            ), onComplete = {
                newRootDropletListScreen()
            }, onError = { throwable ->
                if (!isNavigationEnable) {
                    isNavigationEnable = true
                }
                showErrorMsg(
                    errorMsgCreator.createErrorMsg(throwable),
                    getString(R.string.retry),
                    View.OnClickListener {
                        createCustomServer()
                    })
            }, onPreExecute = {
                isNavigationEnable = false
                showProgress(getString(R.string.setup_server_msg))
            }, onPostExecute = {
                hideProgress()
            }
        )
    }

    private fun newRootDropletListScreen() {
        router.newRootScreen(Screens.DropletListScreen())
    }

    private fun isFieldsValid(): Boolean {
        var isValid = true

        if (userNameField.get().isNullOrEmpty()) {
            userNameError.set(getString(R.string.field_error))
            isValid = false
        } else {
            userNameError.set("")
        }

        if (hostField.get().isNullOrEmpty()) {
            hostError.set(getString(R.string.field_error))
            isValid = false
        } else {
            if (!isIpValid(hostField.get()!!)) {
                hostError.set(getString(R.string.ip_not_valid))
                isValid = false
            } else {
                hostError.set("")
            }
        }

        if (portField.get().isNullOrEmpty()) {
            portError.set(getString(R.string.field_error))
            isValid = false
        } else {
            portError.set("")
        }

        if (authField.get().isNullOrEmpty()) {
            authError.set(getString(R.string.field_error))
            isValid = false
        } else {
            authError.set("")
        }

        return isValid
    }

    companion object {
        private const val PORT = "22"
    }
}