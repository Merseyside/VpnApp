package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

class DropletItemViewModel(private var server: Server) : BaseComparableAdapterViewModel<Server>() {

    override fun isContentTheSame(obj: Server): Boolean {
        return (server == obj)
    }

    override fun isItemsTheSame(obj: Server): Boolean {
        return (server.id == obj.id)
    }

    override fun compareTo(obj: Server): Int {
        return obj.id.compareTo(server.id)
    }

    override fun getItem(): Server {
        return server
    }

    override fun setItem(item: Server) {
        this.server = item

        notifyPropertyChanged(BR.status)
        notifyPropertyChanged(BR.statusColor)
        notifyPropertyChanged(BR.statusIcon)
    }

    fun onClick() {
        getClickListener()?.onItemClicked(server)
    }

    @Bindable
    fun getServerName(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.server_prefix)} ${server.name}"
    }

    @Bindable
    fun getProviderName(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.provider_prefix)} ${server.providerName}"
    }

    @Bindable
    fun getRegion(): String {
        return "${VpnApplication.getInstance().getActualString(R.string.region_prefix)} ${server.regionName}"
    }

    @Bindable
    fun getStatus(): String {
        if (server.connectStatus) {
            return VpnApplication.getInstance().getActualString(R.string.connected)
        }

        return when (server.environmentStatus) {
            SshManager.Status.READY -> {
               VpnApplication.getInstance().getActualString(R.string.connect)
            }

            else -> server.environmentStatus.toString()
        }
    }

    @Bindable
    fun getStatusColor(): Int {
        if (server.connectStatus) {
            return R.attr.colorSecondary
        }

        return when(server.environmentStatus) {
            SshManager.Status.PENDING -> {
                R.attr.pendingColor
            }
            SshManager.Status.IN_PROCESS -> {
                R.attr.colorSecondary
            }
            SshManager.Status.READY -> {
                R.attr.colorPrimary
            }
        }
    }

    @Bindable
    @DrawableRes
    fun getStatusIcon(): Int? {
        if (server.connectStatus) {
            return R.drawable.ic_connected
        }

        return when(server.environmentStatus) {
            SshManager.Status.PENDING -> {
                R.drawable.ic_pending
            }
            SshManager.Status.IN_PROCESS -> {
                R.drawable.ic_process
            }
            SshManager.Status.READY -> {
                R.drawable.ic_ready
            }
        }
    }

    companion object {
        private const val TAG = "DropletItemViewModel"
    }
}