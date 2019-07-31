package com.merseyside.dropletapp.presentation.base

import android.content.Context
import com.upstream.basemvvmimpl.presentation.model.ParcelableViewModel
import ru.terrakok.cicerone.Router

abstract class BaseDropletViewModel(private val router: Router) : ParcelableViewModel() {

    override fun updateLanguage(context: Context) {}

    protected fun back() {
        router.exit()
    }
}