package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model.TypeViewModel
import com.merseyside.adapters.base.BaseSelectableAdapter

class TypeAdapter : BaseSelectableAdapter<LockedType, TypeViewModel>() {

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_free_access_type
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: LockedType): TypeViewModel {
        return TypeViewModel(obj)
    }
}