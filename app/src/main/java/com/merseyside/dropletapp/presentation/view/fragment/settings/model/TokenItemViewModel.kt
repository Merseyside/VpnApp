package com.merseyside.dropletapp.presentation.view.fragment.settings.model

import android.content.Context
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.adapters.model.BaseAdapterViewModel
import com.merseyside.archy.interfaces.IStringHelper

class TokenItemViewModel(override var obj: TokenEntity) : BaseAdapterViewModel<TokenEntity>(obj), IStringHelper {

    @Bindable
    fun getProviderName(): String {
        return Provider.getProviderById(obj.providerId)?.getName() ?: "Wrong provider"
    }

    override fun areItemsTheSame(obj: TokenEntity): Boolean {
        return obj.token == this.obj.token
    }

    @Bindable
    fun isShowDivider(): Boolean {
        return isLast()
    }

    @Bindable
    fun getDeleteText(): String {
        return getString(R.string.delete_action)
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.showDivider)
        notifyPropertyChanged(BR.deleteText)
    }

    override fun getLocaleContext(): Context {
        return VpnApplication.getInstance()
    }
}