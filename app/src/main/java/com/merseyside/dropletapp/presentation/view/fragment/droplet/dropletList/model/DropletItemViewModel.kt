package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

class DropletItemViewModel(override var obj: Server) : BaseComparableAdapterViewModel<Server>(obj) {

    override fun areContentsTheSame(obj: Server): Boolean {
        Log.d(TAG, "here ${obj == this.obj}")
        return (obj == this.obj)
    }

    override fun areItemsTheSame(obj: Server): Boolean {
        Log.d(TAG, "here1 ${obj.id == this.obj.id}")
        return (obj.id == this.obj.id)
    }

    override fun compareTo(obj: Server): Int {
        return obj.id.compareTo(this.obj.id)
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
        Log.d(TAG, "status $obj")

        if (obj.connectStatus) {
            return VpnApplication.getInstance().getActualString(R.string.connected)
        }

        return when (obj.environmentStatus) {
            SshManager.Status.READY -> {
               VpnApplication.getInstance().getActualString(R.string.connect)
            }

            else -> obj.environmentStatus.toString()
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
        }
    }

    companion object {
        private const val TAG = "DropletItemViewModel"
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.status)
        notifyPropertyChanged(BR.statusColor)
        notifyPropertyChanged(BR.statusIcon)
    }
}