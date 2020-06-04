package com.merseyside.dropletapp.presentation.base

import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.domain.Server
import com.merseyside.merseyLib.utils.mvvm.SingleLiveEvent
import de.blinkt.openvpn.VpnProfile
import ru.terrakok.cicerone.Router

abstract class BaseVpnViewModel(router: Router? = null) : BaseDropletViewModel(router) {

    lateinit var server: Server
        protected set

    val vpnProfileLiveData = MutableLiveData<VpnProfile>()

    val connectionLiveData = SingleLiveEvent<Boolean>()

    var sessionTime: Long = 0

    protected var isConnected = false
        set(value) {
            if (field != value) {
                field = value

                connectionLiveData.value = value
            }
        }
}