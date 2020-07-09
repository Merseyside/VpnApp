package com.merseyside.dropletapp.presentation.view.fragment.auth.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.presentation.view.fragment.auth.model.ProviderItemModel
import com.merseyside.adapters.base.BaseSortedAdapter

class ProviderAdapter : BaseSortedAdapter<OAuthProvider, ProviderItemModel>() {

    override fun createItemViewModel(obj: OAuthProvider): ProviderItemModel {
        return ProviderItemModel(obj)
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_provider
    }
}