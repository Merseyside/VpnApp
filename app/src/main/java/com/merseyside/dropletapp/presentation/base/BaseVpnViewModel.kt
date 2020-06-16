package com.merseyside.dropletapp.presentation.base

import com.merseyside.dropletapp.domain.Server
import com.merseyside.merseyLib.utils.mvvm.SingleLiveEvent
import ru.terrakok.cicerone.Router

abstract class BaseVpnViewModel(
    router: Router? = null
) : BaseDropletViewModel(router) {

    lateinit var server: Server
        protected set

    val connectionLiveData = SingleLiveEvent<Boolean>()
    val vpnNotPreparedLiveEvent = SingleLiveEvent<Any>()

    protected var isConnected = false
        set(value) {
            if (field != value) {
                field = value

                connectionLiveData.value = value
            }
        }

    abstract fun onConnect()
}