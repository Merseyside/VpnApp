package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

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

        return when {
            this.obj.connectStatus == obj.connectStatus -> {
                this.obj.id.compareTo(obj.id)
            }
            this.obj.connectStatus -> -1
            else -> 1
        }
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

    @Bindable
    fun getStatus(): String {
        if (obj.connectStatus) {
            return VpnApplication.getInstance().getActualString(R.string.connected)
        }

        return when (obj.environmentStatus) {
            SshManager.Status.READY -> VpnApplication.getInstance().getActualString(R.string.connect)
            SshManager.Status.ERROR -> VpnApplication.getInstance().getActualString(R.string.error)
            SshManager.Status.IN_PROCESS -> VpnApplication.getInstance().getActualString(R.string.in_process)
            SshManager.Status.PENDING -> VpnApplication.getInstance().getActualString(R.string.pending)
        }
    }

    @Bindable
    fun getStatusColor(): Int {
        if (obj.connectStatus) {
            return R.attr.colorSecondary
        }

        return when(obj.environmentStatus) {
            SshManager.Status.PENDING -> {
                R.attr.pendingColor
            }
            SshManager.Status.IN_PROCESS -> {
                R.attr.colorSecondary
            }
            SshManager.Status.READY -> {
                R.attr.colorPrimary
            }
            SshManager.Status.ERROR -> {
                R.attr.colorError
            }
        }
    }


    @DrawableRes
    @Bindable
    fun getStatusIcon(): Int? {
        if (obj.connectStatus) {
            return R.drawable.ic_connected
        }

        return when(obj.environmentStatus) {
            SshManager.Status.PENDING -> {
                R.drawable.ic_pending
            }
            SshManager.Status.IN_PROCESS -> {
                R.drawable.ic_process
            }
            SshManager.Status.READY -> {
                R.drawable.ic_ready
            }
            SshManager.Status.ERROR -> {
                R.drawable.ic_error
            }
        }
    }

    @Bindable
    fun getShareVisibility(): Boolean {
        return obj.environmentStatus == SshManager.Status.READY
    }

    fun onShareClick() {
        onShareClickListener?.onShareOvpn(obj)
    }

    companion object {
        private const val TAG = "DropletItemViewModel"
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.status)
        notifyPropertyChanged(BR.statusColor)
        notifyPropertyChanged(BR.statusIcon)
        notifyPropertyChanged(BR.shareVisibility)
    }
}