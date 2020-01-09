package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model.TypeNameItemViewModel
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseSelectableAdapter

class TypesAdapter : BaseSelectableAdapter<String, TypeNameItemViewModel>() {

    override fun isAllowToCancelSelection(): Boolean {
        return false
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_type
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: String): TypeNameItemViewModel {
        return TypeNameItemViewModel(obj)
    }
}