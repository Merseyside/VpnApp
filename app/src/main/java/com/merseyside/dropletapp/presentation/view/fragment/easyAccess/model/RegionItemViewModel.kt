package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.adapters.model.BaseAdapterViewModel

class RegionItemViewModel(obj: Region) : BaseAdapterViewModel<Region>(obj) {

    override fun areItemsTheSame(obj: Region): Boolean {
        return obj.name == this.obj.name
    }

    override fun notifyUpdate() {}

    @Bindable
    fun getName(): String {
        return obj.name
    }

    @Bindable
    fun getCountryImage(): String {
        return obj.code
    }

    @Bindable
    @DrawableRes
    fun getConnectionImage(): Int {
        return when(obj.connectionLevel) {
            0 -> R.drawable.red
            1 -> R.drawable.orange
            2 -> R.drawable.yellow
            3 -> R.drawable.green
            else -> throw IllegalArgumentException()
        }
    }

    @Bindable
    fun isLock(): Boolean {
        return obj.isLocked
    }
}