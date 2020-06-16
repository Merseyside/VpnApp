package com.merseyside.dropletapp.presentation.base

import android.content.Context
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.merseyside.merseyLib.presentation.model.ParcelableViewModel
import com.merseyside.merseyLib.utils.mvvm.SingleLiveEvent
import org.apache.xpath.operations.Bool
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router? = null) : ParcelableViewModel() {

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