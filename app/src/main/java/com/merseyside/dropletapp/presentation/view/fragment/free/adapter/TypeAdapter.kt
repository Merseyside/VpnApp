package com.merseyside.dropletapp.presentation.view.fragment.free.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.fragment.free.model.Type
import com.merseyside.dropletapp.presentation.view.fragment.free.model.TypeViewModel
import com.merseyside.merseyLib.adapters.BaseSelectableAdapter

class TypeAdapter : BaseSelectableAdapter<Type, TypeViewModel>() {

    init {
        add(populateTypes())
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_free_access_type
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: Type): TypeViewModel {
        return TypeViewModel(obj)
    }

    companion object {
        fun populateTypes(): List<Type> {
            return listOf(
                Type(
                    name = "OpenVPN",
                    isLocked = false
                ),

                Type(
                    name = "WireGuard",
                    isLocked = true
                ),

                Type(
                    name = "L2TP",
                    isLocked = true
                ),

                Type(
                    name = "ShadowSocks",
                    isLocked = true
                ),

                Type(
                    name = "PPTP",
                    isLocked = true
                )
            )
        }
    }
}