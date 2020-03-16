package com.merseyside.dropletapp.presentation.base

import android.app.Application
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.merseyside.mvvmcleanarch.presentation.model.ParcelableViewModel
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router? = null) : ParcelableViewModel() {

    protected val errorMsgCreator = ErrorMessageFactory()

    protected var isNavigationEnable = true

    override val application: Application
        get() = VpnApplication.getInstance()

    fun goBack() {
        if (isNavigationEnable) {
            router?.exit()
        }
    }
}