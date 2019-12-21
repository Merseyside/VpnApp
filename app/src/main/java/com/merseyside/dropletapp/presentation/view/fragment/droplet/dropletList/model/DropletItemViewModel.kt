package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.mvvmcleanarch.presentation.model.BaseComparableAdapterViewModel
import com.merseyside.mvvmcleanarch.utils.Logger

class DropletItemViewModel(override var obj: Server) : BaseComparableAdapterViewModel<Server>(obj) {

    override fun areContentsTheSame(obj: Server): Boolean {
        return (this.obj == obj).also { Logger.log(this, it) }
    }

    override fun areItemsTheSame(obj: Server): Boolean {
        return obj.id == this.obj.id
    }

    override fun compareTo(obj: Server): Int {

        return this.obj.id.compareTo(obj.id)
    }

    @Bindable
    fun getServerName(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.server_prefix)} ${obj.name}"
    }

    @Bindable
    fun getProviderName(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.provider_prefix)} ${obj.providerName}"
    }

    @Bindable
    fun getRegion(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.region_prefix)} ${obj.regionName}"
    }

    override fun notifyUpdate() {
        Logger.log(this, "notify")
    }
}