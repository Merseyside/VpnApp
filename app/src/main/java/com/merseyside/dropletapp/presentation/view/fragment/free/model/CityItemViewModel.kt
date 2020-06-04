package com.merseyside.dropletapp.presentation.view.fragment.free.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.merseyLib.model.BaseAdapterViewModel
import org.apache.xpath.operations.Bool

class CityItemViewModel(override var obj: City) : BaseAdapterViewModel<City>(obj) {

    override fun areItemsTheSame(obj: City): Boolean {
        return obj.name == this.obj.name
    }

    override fun notifyUpdate() {}

    @Bindable
    fun getName(): String {
        return obj.name
    }

    @Bindable
    fun getCountryImage(): String {
        return obj.country
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