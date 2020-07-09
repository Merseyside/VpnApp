package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.adapters.model.BaseSelectableAdapterViewModel

class TypeNameItemViewModel(override var obj: String) : BaseSelectableAdapterViewModel<String>(obj) {

    override fun areItemsTheSame(obj: String): Boolean {
        return this.obj == obj
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.itemBackground)
    }

    @Bindable
    @DrawableRes
    fun getItemBackground(): Int {
        return if (isSelected()) {
            R.drawable.selected_type_bg
        } else {
            R.drawable.type_bg
        }
    }

    @Bindable
    fun getTypeName(): String {
        return obj
    }

    override fun areContentsTheSame(obj: String): Boolean {
        return false
    }

    override fun compareTo(obj: String): Int {
        return 0
    }

    override fun notifySelectEnabled(isEnabled: Boolean) {}

    override fun onSelectedChanged(isSelected: Boolean) {
        notifyUpdate()
    }

}