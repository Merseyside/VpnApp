package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.shadowsocks.plugin.PluginManager
import com.merseyside.archy.presentation.view.localeViews.LocaleData
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
import com.merseyside.utils.Logger
import com.merseyside.utils.ext.isNotNullAndEmpty
import com.merseyside.utils.ext.log
import com.merseyside.utils.ext.onChange
import com.merseyside.utils.mvvm.SingleLiveEvent
import com.merseyside.utils.time.getDate
import com.merseyside.utils.time.getFormattedDate
import com.merseyside.utils.time.toTimeUnit
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
    private var currentType: LockedType? = null

    val connectButtonDrawable = ObservableField<Int>(R.drawable.background_action_button_idle)
    val connectButtonTitle = ObservableField(getString(R.string.connect))
    val types = ObservableField<List<LockedType>>()
    val timer = ObservableField<String>()

    val currentRegion = ObservableField<Region>().apply {
        onChange { _, value, isInitial ->
            if (!isInitial) {
                isNewRegion.set(ServiceConnectionType.isActive()
                        && (server.regionName != value?.code)
                )
            }
        }
    }

    private val regionLiveData = MutableLiveData<List<Region>>()
    val v2RayRequireEvent = SingleLiveEvent<Any>()
    val info = ObservableField<LocaleData>()

    fun getRegionLiveData(): LiveData<List<Region>> = regionLiveData

    val isPurchased = ObservableField(false)
    val isNewRegion = ObservableField(false)

    private var connectionType: ServiceConnectionType? = null
        set(value) {
            field = value

            async {
                val isSubscribed = subscriptionManager.isSubscribed()

                val alreadySpendTime =  if (isSubscribed) {
                    0L
                } else {
                    prefsHelper.getTrialTime()
                }

                value?.apply {
                    setConnectionCallback(object : ConnectionCallback {

                        override fun onConnectionEvent(connectionLevel: ConnectionLevel) {
                            setConnectionStatus(connectionLevel)
                        }

                        override fun onSessionTime(timestamp: Long) {
                            sessionTime = timestamp

                            val total = timestamp + alreadySpendTime

                            if (!isSubscribed) {
                                if (total >= hour.toMillisLong()) {

                                    showTrialAlertDialog()

                                    if (isConnected) {
                                        connectionType!!.stop()
                                    }
                                }
                            }

                            val formatted = getFormattedDate(total, "HH : mm : ss")
                            timer.set(formatted)
                        }
                    })

                    if (ServiceConnectionType.isActive()) {
                        this@EasyAccessViewModel.server = value.server!!
                        setConnectionStatus(ServiceConnectionType.getCurrentStatus())
                    } else {
                        start(this@EasyAccessViewModel.server)
                    }
                }
            }
        }

    init {
        if (ServiceConnectionType.isActive()) {
            connectionType = ServiceConnectionType.getCurrentConnectionType()
        }

        getRegions()
        getLockedTypes()
        getPurchasedSubscription()
    }

    private fun setCurrentRegion() {
        if (connectionType != null && regionLiveData.value.isNotNullAndEmpty()) {

            val foundRegion = regionLiveData.value!!.find { it.code == server.regionName }

            if (foundRegion != null) {
                currentRegion.set(foundRegion)
            }
        }
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
        createServer(type, config)

        if (server.typedConfig is TypedConfig.Shadowsocks) {
            if ((server.typedConfig as TypedConfig.Shadowsocks).isV2Ray()) {
                if (!PluginManager.isV2RayEnabled()) {
                    v2RayRequireEvent.call()
                    return
                }
            }
        }

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
                    locale = application.getLocale().language,
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

    private fun getRegions() {
        getRegionsUseCase.execute(
            onComplete = {
                if (it.isNotEmpty()) {
                    regionLiveData.value = it

                    setCurrentRegion()
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

                launch {

                    if (!subscriptionManager.isSubscribed()) {
                        prefsHelper.addTrialTime(sessionTime)
                    }

                    sessionTime = 0
                }
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
                R.drawable.background_action_button_disconnect
            }

            ConnectionLevel.IDLE -> {
                R.drawable.background_button_gradient
            }

            else -> {
                R.drawable.background_action_button_connecting
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
            if (!ServiceConnectionType.isActive()) {

                launch {
                    if (subscriptionManager.isSubscribed() ||
                        prefsHelper.getTrialTime().log() < hour.toMillisLong()) {
                        connect()
                    } else {
                        showTrialAlertDialog()
                    }
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

                    val expiryTime = it.expiryTime.toTimeUnit().getDate()
                    info.set(LocaleData(R.string.subscription_expity_date, expiryTime))
                }
            } else {
                info.set(LocaleData(R.string.trial_msg)).log()
            }
        }
    }

    private fun createServer(type: Type, config: String? = null) {
        this.server = Server.newServer(
            type = type,
            region = currentRegion.get()!!.code,
            config = config
        )
    }

    fun onChangeRegion() {
        connectionType?.stop()
        onConnect()

        isNewRegion.set(false)
    }

    companion object {
        private val hour = Hours(1)
    }
}