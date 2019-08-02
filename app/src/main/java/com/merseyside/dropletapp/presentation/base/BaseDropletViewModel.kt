package com.merseyside.dropletapp.presentation.base

import android.content.Context
import androidx.annotation.StringRes
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.exception.ErrorMessageFactory
import com.upstream.basemvvmimpl.presentation.model.ParcelableViewModel
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router? = null) : ParcelableViewModel() {

    protected val errorMsgCreator = ErrorMessageFactory(VpnApplication.getInstance())

    override fun updateLanguage(context: Context) {}

    protected fun back() {
        router?.exit()
    }

    protected fun getString(@StringRes id: Int): String {
        return VpnApplication.getInstance().getString(id)
    }
}