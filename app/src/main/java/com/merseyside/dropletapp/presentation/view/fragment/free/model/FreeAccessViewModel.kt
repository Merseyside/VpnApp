package com.merseyside.dropletapp.presentation.view.fragment.free.model

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.GetVpnConfigInteractor
import com.merseyside.dropletapp.presentation.base.BaseVpnViewModel
import com.merseyside.dropletapp.utils.PrefsHelper
import com.merseyside.kmpMerseyLib.domain.coroutines.applicationContext
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.utils.ext.log
import com.merseyside.merseyLib.utils.time.getCurrentTimeMillis
import com.merseyside.merseyLib.utils.time.getFormattedDate
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.UpstreamConfigParser
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.*
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

class FreeAccessViewModel(
    private val router: Router,
    private val prefsHelper: PrefsHelper,
    private val getVpnConfigUseCase: GetVpnConfigInteractor
) : BaseVpnViewModel(router), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = applicationContext

    val connectButtonDrawable = ObservableField<Int>()
    val connectButtonTitle = ObservableField(getString(R.string.connect))
    val timer = ObservableField<String>()

    var timerJob: Job? = null

    init {
        server = Server.newDummyServer()
    }

    override fun dispose() {
        getVpnConfigUseCase.cancel()
        stopTimer()
    }

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    private fun loadVpnProfile(body: String): VpnProfile? {
        return UpstreamConfigParser.parseConfig(getLocaleContext(), body)
    }

    private fun prepareVpn(body: String) {
        val vpnProfile: VpnProfile? = loadVpnProfile(body)
        if (vpnProfile != null) {
            vpnProfileLiveData.value = vpnProfile
        }
    }

    private fun connect() {
        getVpnConfigUseCase.execute(
            params = GetVpnConfigInteractor.Params(typeName = "ovpn"),
            onComplete = { config ->
                Logger.log(this, config)
                prepareVpn(config)
            }, onError = {
                if (it is TrialIsOverException) {
                    showTrialAlertDialog()
                } else {
                    showErrorMsg("Error")
                }
            }
        )
    }

    fun setConnectionStatus(status: VpnStatus.ConnectionStatus) {
        updateConnectButtonWithVpnStatus(status)

        when (status) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                this.isConnected = true
                startTimer()
            }

            else -> {
                this.isConnected = false
                stopTimer()
            }
        }
    }

    private fun updateConnectButtonWithVpnStatus(status: VpnStatus.ConnectionStatus) {
        connectButtonDrawable.set(getConnectButtonDrawable(status))
        connectButtonTitle.set(getConnectButtonTitle(status))
    }

    @DrawableRes
    private fun getConnectButtonDrawable(status: VpnStatus.ConnectionStatus): Int {
        return when(status) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                R.attr.colorError
            }

            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED -> {
                R.attr.colorPrimary
            }

            else -> {
                R.attr.pendingColor
            }
        }
    }

    private fun getConnectButtonTitle(serverStatus: VpnStatus.ConnectionStatus): String {
        return when(serverStatus) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                getString(R.string.disconnect_action)
            }

            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED -> {
                getString(R.string.connect)
            }

            else -> {
                getString(R.string.connecting)
            }
        }
    }

    fun onConnect() {
        if (!isConnected) {
            if (prefsHelper.getTrialTime() < hour.toMillisLong()) {
                connect()
            } else {
                showTrialAlertDialog()
            }
        } else {
            isConnected = false
        }
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

    private fun getDelta(): Long {
        return getCurrentTimeMillis() - sessionTime
    }

    fun startTimer() {
        if (timerJob == null) {
            timerJob = launch {

                val alreadySpendTime = prefsHelper.getTrialTime()

                while (isActive) {
                    val total = getDelta() + alreadySpendTime
                    if (total >= hour.toMillisLong()) {
                        stopTimer()

                        showTrialAlertDialog()

                        if (isConnected) {
                            isConnected = false
                        }
                    }

                    val formatted = getFormattedDate(total, "HH : mm : ss")
                    timer.set(formatted)

                    delay(1000)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.run {
            cancel()
            timerJob = null

            prefsHelper.addTrialTime(getDelta())
        }
    }

    companion object {
        private val hour = Hours(1)
    }
}