package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.mvvmcleanarch.presentation.model.BaseComparableAdapterViewModel

class DropletItemViewModel(override var obj: Server) : BaseComparableAdapterViewModel<Server>(obj) {

    interface OnShareClickListener {
        fun onShareOvpn(server: Server)
    }

    private var onShareClickListener: OnShareClickListener? = null

    fun setOnShareClickListener(listener: OnShareClickListener?) {
        this.onShareClickListener = listener
    }

    override fun areContentsTheSame(obj: Server): Boolean {
        return (obj == this.obj)
    }

    override fun areItemsTheSame(obj: Server): Boolean {
        return (obj.id == this.obj.id)
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

    override fun notifyUpdate() {}
}