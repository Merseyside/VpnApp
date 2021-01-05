package com.merseyside.dropletapp.presentation.base

import android.app.Application
import android.content.Context
import com.merseyside.archy.presentation.model.ParcelableViewModel
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.merseyside.utils.mvvm.SingleLiveEvent
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(
    application: Application,
    private val router: Router? = null
) : ParcelableViewModel(application) {

    protected val errorMsgCreator = ErrorMessageFactory()

    protected var isNavigationEnable = true

    val onBackSingleEvent = SingleLiveEvent<Any>()

    override fun getLocaleContext(): Context {
        return VpnApplication.getInstance()
    }

    fun goBack(isNotify: Boolean = true): Boolean {
        return if (onBackPressed()) {
            if (isNotify) {
                onBackSingleEvent.call()
            }
            router?.exit()
            true
        } else {
            false
        }
    }
}