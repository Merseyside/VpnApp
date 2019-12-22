package com.merseyside.dropletapp.presentation.base

import androidx.annotation.StringRes
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.merseyside.mvvmcleanarch.presentation.model.ParcelableViewModel
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router? = null) : ParcelableViewModel() {

    protected val errorMsgCreator = ErrorMessageFactory()

    fun goBack() {
        router?.exit()
    }

    protected fun getString(@StringRes id: Int, vararg args: String): String {
        return getString(VpnApplication.getInstance(), id, *args)
    }
}