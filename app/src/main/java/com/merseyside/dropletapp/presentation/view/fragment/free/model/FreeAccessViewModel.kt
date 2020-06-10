package com.merseyside.dropletapp.presentation.view.fragment.free.model

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.connectionTypes.*
import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.connectionTypes.typeImpl.openVpn.OpenVpnConnectionType
import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.GetLockedTypesInteractor
import com.merseyside.dropletapp.domain.interactor.GetVpnConfigInteractor
import com.merseyside.dropletapp.presentation.base.BaseVpnViewModel
import com.merseyside.dropletapp.utils.PrefsHelper
import com.merseyside.dropletapp.utils.application
import com.merseyside.kmpMerseyLib.domain.coroutines.applicationContext
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.utils.ext.log
import com.merseyside.merseyLib.utils.time.getFormattedDate
import kotlinx.coroutines.*
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class FreeAccessViewModel(
    private val router: Router,
    private val prefsHelper: PrefsHelper,
    private val connectionTypeBuilder: Builder,
    private val getVpnConfigUseCase: GetVpnConfigInteractor,
    private val getLockedTypesUseCase: GetLockedTypesInteractor
) : BaseVpnViewModel(router), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = applicationContext

    var sessionTime: Long = 0
    var currentType: LockedType? = null

    val connectButtonDrawable = ObservableField<Int>()
    val connectButtonTitle = ObservableField(getString(R.string.connect))
    val types = ObservableField<List<LockedType>>()
    val timer = ObservableField<String>()

    private var connectionType: ServiceConnectionType? = null
        set(value) {
            field = value

            value?.apply {
                setConnectionCallback(object : ConnectionCallback {
                    override fun onConnectionEvent(connectionLevel: ConnectionLevel) {
                        setConnectionStatus(connectionLevel)
                    }

                    override fun onSessionTime(timestamp: Long) {
                        sessionTime = timestamp

                        val alreadySpendTime = prefsHelper.getTrialTime()

                        val total = timestamp + alreadySpendTime
                        if (total >= hour.toMillisLong()) {

                            showTrialAlertDialog()

                            if (isConnected) {
                                connectionType!!.stop()
                            }
                        }

                        val formatted = getFormattedDate(total, "HH : mm : ss")
                        timer.set(formatted)
                    }
                })

                start(this@FreeAccessViewModel.server)
            }
        }

    init {
        server = Server.newDummyServer()

        if (ServiceConnectionType.isActive()) {
            connectionType = ServiceConnectionType.getCurrentConnectionType()
        }

        getLockedTypes()
    }

    override fun dispose() {
        getVpnConfigUseCase.cancel()
        getLockedTypesUseCase.cancel()
    }

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    private fun getLockedTypes() {
        getLockedTypesUseCase.execute(
            onComplete = { types.set(it)}
        )
    }

    private fun startVpn(type: Type, config: String) {
        connectionType = connectionTypeBuilder
            .setType(type)
            .setConfig(config)
            .build()
    }

    private fun connect() {
        val type = currentType!!.type
        getVpnConfigUseCase.execute(
            params = GetVpnConfigInteractor.Params(type = type),
            onComplete = { config ->
                startVpn(type, config)
            }, onError = {
                if (it is TrialIsOverException) {
                    showTrialAlertDialog()
                } else {
                    showErrorMsg("Error")
                }
            }
        )
    }

    fun setConnectionStatus(level: ConnectionLevel) {
        Logger.log(this, level)
        updateConnectButtonWithVpnStatus(level)

        when (level) {
            ConnectionLevel.CONNECTED -> {
                this.isConnected = true
            }

            else -> {
                this.isConnected = false
                prefsHelper.addTrialTime(sessionTime)
                sessionTime = 0
            }
        }
    }

    private fun updateConnectButtonWithVpnStatus(level: ConnectionLevel) {
        connectButtonDrawable.set(getConnectButtonDrawable(level))
        connectButtonTitle.set(getConnectButtonTitle(level))
    }

    @DrawableRes
    private fun getConnectButtonDrawable(level: ConnectionLevel): Int {
        return when(level) {
            ConnectionLevel.CONNECTED -> {
                R.attr.colorError
            }

            ConnectionLevel.DISCONNECTED -> {
                R.attr.colorPrimary
            }

            else -> {
                R.attr.pendingColor
            }
        }
    }

    private fun getConnectButtonTitle(level: ConnectionLevel): String {
        return when(level) {
            ConnectionLevel.CONNECTED -> {
                getString(R.string.disconnect_action)
            }

            ConnectionLevel.DISCONNECTED -> {
                getString(R.string.connect)
            }

            else -> {
                getString(R.string.connecting)
            }
        }
    }

    override fun onConnect() {

        if (OpenVpnConnectionType.prepare(application) == null) {
            if (!isConnected) {
                if (prefsHelper.getTrialTime().log() < hour.toMillisLong()) {
                    connect()
                } else {
                    showTrialAlertDialog()
                }
            } else {
                connectionType?.stop()
            }
        } else {
            vpnNotPreparedLiveEvent.call()
        }
    }

    fun onTypeChanged(type: Any) {
        currentType = type as LockedType
    }

    private fun showTrialAlertDialog() {
        showAlertDialog(
            titleRes = R.string.trial_is_over,
            messageRes = R.string.trial_is_over_msg,
            positiveButtonTextRes = R.string.log_in,
            negativeButtonTextRes = R.string.cancel,
            onPositiveClick = { goBack(isNotify = true) }
        )
    }

    companion object {
        private val hour = Hours(1)
    }
}