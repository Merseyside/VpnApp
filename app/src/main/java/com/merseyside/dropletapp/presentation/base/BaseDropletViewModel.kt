package com.merseyside.dropletapp.presentation.base

import android.content.Context
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.merseyside.merseyLib.presentation.model.ParcelableViewModel
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router? = null) : ParcelableViewModel() {

    protected val errorMsgCreator = ErrorMessageFactory()

    protected var isNavigationEnable = true

    override fun getLocaleContext(): Context {
        return VpnApplication.getInstance()
    }

    fun goBack() {
        if (isNavigationEnable) {
            router?.exit()
        }
    }
}