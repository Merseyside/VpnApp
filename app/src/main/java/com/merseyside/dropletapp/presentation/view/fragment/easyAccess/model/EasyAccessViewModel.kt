package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.shadowsocks.plugin.PluginManager
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.connectionTypes.*
import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.GetLockedTypesInteractor
import com.merseyside.dropletapp.domain.interactor.easyAccess.GetRegionsInteractor
import com.merseyside.dropletapp.domain.interactor.easyAccess.GetVpnConfigInteractor
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.presentation.base.BaseVpnViewModel
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.PrefsHelper
import com.merseyside.dropletapp.utils.application
import com.merseyside.kmpMerseyLib.domain.coroutines.applicationContext
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.utils.ext.log
import com.merseyside.merseyLib.utils.mvvm.SingleLiveEvent
import com.merseyside.merseyLib.utils.time.getFormattedDate
import kotlinx.coroutines.*
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class EasyAccessViewModel(
    private val router: Router,
    private val prefsHelper: PrefsHelper,
    private val connectionTypeBuilder: Builder,
    private val getVpnConfigUseCase: GetVpnConfigInteractor,
    private val getLockedTypesUseCase: GetLockedTypesInteractor,
    private val getRegionsUseCase: GetRegionsInteractor,
    private val subscriptionManager: SubscriptionManager
) : BaseVpnViewModel(router), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = applicationContext

    var sessionTime: Long = 0
    var currentType: LockedType? = null

    val connectButtonDrawable = ObservableField<Int>()
    val connectButtonTitle = ObservableField(getString(R.string.connect))
    val types = ObservableField<List<LockedType>>()
    val timer = ObservableField<String>()

    val currentRegion = ObservableField<Region>()

    val regionLiveData = MutableLiveData<List<Region>>()
    val v2RayRequireEvent = SingleLiveEvent<Any>()

    fun getRegionLiveData(): LiveData<List<Region>> = regionLiveData

    val isPurchased = ObservableField(false)

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

                start(this@EasyAccessViewModel.server)
            }
        }

    init {
        server = Server.newDummyServer()

        if (ServiceConnectionType.isActive()) {
            connectionType = ServiceConnectionType.getCurrentConnectionType()
        }

        getRegions()
        getLockedTypes()
        getPurchasedSubscription()
    }

    override fun dispose() {
        getVpnConfigUseCase.cancel()
        getLockedTypesUseCase.cancel()
        getRegionsUseCase.cancel()
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
        val type = currentType?.type

        if (type != null) {
            getVpnConfigUseCase.execute(
                params = GetVpnConfigInteractor.Params(
                    type = type,
                    locale = application.getLocale().language.log(),
                    region = currentRegion.get()!!
                ),
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
    }

    fun getRegions() {
        getRegionsUseCase.execute(
            onComplete = {
                if (it.isNotEmpty()) {
                    regionLiveData.value = it
                }
            }, onError = {
                showErrorMsg("Something went wrong")
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

    fun onPurchased() {

        getPurchasedSubscription()
        getRegions()
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

            ConnectionLevel.IDLE -> {
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

            ConnectionLevel.IDLE -> {
                getString(R.string.connect)
            }

            else -> {
                getString(R.string.connecting)
            }
        }
    }

    override fun onConnect() {
        if (VpnHelper.prepare(application) == null) {
            if (!isConnected) {

                if (server.typedConfig is TypedConfig.Shadowsocks) {
                    if ((server.typedConfig as TypedConfig.Shadowsocks).isV2Ray()) {
                        if (!PluginManager.isV2RayEnabled()) {
                            v2RayRequireEvent.call()
                            return
                        }
                    }
                }

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

    private fun getPurchasedSubscription() {
        launch {
            if (subscriptionManager.isSubscribed()) {
                subscriptionManager.getSubscriptionInfo()?.let {
                    isPurchased.set(true)
                }
            }
        }
    }

    companion object {
        private val hour = Hours(1)
    }
}