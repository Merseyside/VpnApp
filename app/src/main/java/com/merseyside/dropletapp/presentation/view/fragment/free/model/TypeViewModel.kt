package com.merseyside.dropletapp.presentation.view.fragment.free.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.BR
import com.merseyside.merseyLib.model.BaseSelectableAdapterViewModel
import org.apache.xpath.operations.Bool

class TypeViewModel(override var obj: Type) : BaseSelectableAdapterViewModel<Type>(
    obj,
    isSelectable = !obj.isLocked
) {

    override fun notifySelectEnabled(isEnabled: Boolean) {}

    override fun onSelectedChanged(isSelected: Boolean) {
        notifyUpdate()
    }

    override fun areContentsTheSame(obj: Type): Boolean {
        return false
    }

    override fun compareTo(obj: Type): Int {
        return 0
    }

    override fun areItemsTheSame(obj: Type): Boolean {
        return false
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.itemBackground)
    }

    @Bindable
    fun getName(): String {
        return obj.name
    }

    @Bindable
    fun isLocked(): Boolean {
        return obj.isLocked
    }

    @Bindable
    @DrawableRes
    fun getItemBackground(): Int {
        return if (isSelected()) {
            R.drawable.free_selected_type_bg
        } else {
            R.drawable.free_type_bg
        }
    }
}