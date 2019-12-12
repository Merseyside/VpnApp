package com.merseyside.dropletapp.presentation.view.fragment.settings.model

import androidx.databinding.Bindable
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.providerApi.Provider
import com.upstream.basemvvmimpl.presentation.model.BaseAdapterViewModel

class TokenItemViewModel(override var obj: TokenEntity) : BaseAdapterViewModel<TokenEntity>(obj) {

    @Bindable
    fun getName(): String {
        return obj.name
    }

    @Bindable
    fun getProviderName(): String {
        return Provider.getProviderById(obj.providerId)?.getName() ?: "Wrong provider"
    }

    override fun areItemsTheSame(obj: TokenEntity): Boolean {
        return obj.token == this.obj.token
    }

    override fun notifyUpdate() {}
}