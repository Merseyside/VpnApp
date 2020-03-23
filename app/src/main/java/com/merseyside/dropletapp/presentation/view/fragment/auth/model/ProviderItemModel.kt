package com.merseyside.dropletapp.presentation.view.fragment.auth.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.utils.getProviderColor
import com.merseyside.dropletapp.utils.getProviderIcon
import com.merseyside.mvvmcleanarch.presentation.model.BaseComparableAdapterViewModel
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.ext.isNotNullAndEmpty

class ProviderItemModel(override var obj: OAuthProvider) : BaseComparableAdapterViewModel<OAuthProvider>(obj) {

    override fun areItemsTheSame(obj: OAuthProvider): Boolean {
        return obj.provider.getId() == this.obj.provider.getId()
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.authStatusIcon)
    }

    @Bindable
    @DrawableRes
    fun getIcon(): Int {
        return getProviderIcon(obj.provider.getId())
    }

    @Bindable
    fun getTitle(): String {
        return obj.provider.getName()
    }

    @Bindable
    @ColorRes
    fun getTitleColor(): Int {
        return getProviderColor(obj.provider.getId())
    }

    @Bindable
    @DrawableRes
    fun getAuthStatusIcon(): Int {
        return if (obj.token.isNullOrEmpty()) {
            R.drawable.ic_auth
        } else {
            R.drawable.ic_ready
        }
    }

    override fun areContentsTheSame(obj: OAuthProvider): Boolean {
        return this.obj == obj
    }

    override fun compareTo(obj: OAuthProvider): Int {

        return if (obj.token.isNotNullAndEmpty() && this.obj.token.isNotNullAndEmpty()) {
            this.obj.provider.getId().compareTo(obj.provider.getId())
        } else if (obj.token.isNotNullAndEmpty()){
            1
        } else if (this.obj.token.isNotNullAndEmpty()) {
            -1
        } else {
            this.obj.provider.getId().compareTo(obj.provider.getId())
        }
    }
}