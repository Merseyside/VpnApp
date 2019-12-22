package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.mvvmcleanarch.presentation.adapter.SelectableItemInterface
import com.merseyside.mvvmcleanarch.presentation.model.BaseAdapterViewModel

class TypeNameItemViewModel(override var obj: String) : BaseAdapterViewModel<String>(obj), SelectableItemInterface {

    override fun areItemsTheSame(obj: String): Boolean {
        return this.obj == obj
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.itemBackground)
    }

    override var isSelected: Boolean = false
        set(value) {
            field = value

            notifyUpdate()
        }

    @Bindable
    @DrawableRes
    fun getItemBackground(): Int {
        return if (isSelected) {
            R.drawable.selected_type_bg
        } else {
            R.drawable.type_bg
        }
    }

    @Bindable
    fun getTypeName(): String {
        return obj
    }

}