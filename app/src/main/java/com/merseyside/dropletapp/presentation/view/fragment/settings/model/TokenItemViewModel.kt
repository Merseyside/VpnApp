package com.merseyside.dropletapp.presentation.view.fragment.settings.model

import androidx.databinding.Bindable
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.providerApi.Provider
import com.upstream.basemvvmimpl.presentation.model.BaseAdapterViewModel

class TokenItemViewModel(private var token: TokenEntity) : BaseAdapterViewModel<TokenEntity>() {

    override fun getItem(): TokenEntity {
        return token
    }

    override fun setItem(item: TokenEntity) {
        this.token = item
    }

    @Bindable
    fun getName(): String {
        return token.name
    }

    @Bindable
    fun getProviderName(): String {
        return Provider.getProviderById(token.providerId)?.getName() ?: "Wrong provider"
    }

    fun onDeleteClicked() {
        getClickListener()?.onItemClicked(token)
    }
}