package com.merseyside.dropletapp.presentation.base

import android.app.Application
import com.merseyside.dropletapp.domain.Server
import com.merseyside.utils.mvvm.SingleLiveEvent
import ru.terrakok.cicerone.Router

abstract class BaseVpnViewModel(
    application: Application,
    router: Router? = null
) : BaseDropletViewModel(application, router) {

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