package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.utils.getProviderIcon
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
    fun getServerIp(): String {
        return getString(R.string.address, obj.address)
    }

    @Bindable
    fun getConnectionType(): String {
        return getString(R.string.type, obj.typedConfig.getName())
    }

    @Bindable
    fun getRegion(): String {
        return obj.regionName?.let {
            getString(R.string.region, obj.regionName!!)
        } ?: ""
    }

    @DrawableRes
    fun getIcon(): Int {
        return getProviderIcon(obj.providerId)
    }

    @Bindable
    @ColorRes
    fun getStatusColor(): Int {

        return when(obj.environmentStatus) {
            SshManager.Status.STARTING -> {
                R.attr.colorPrimary
            }
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

    @Bindable
    @DrawableRes
    fun getStatusIcon(): Int {

        return when(obj.environmentStatus) {
            SshManager.Status.STARTING -> {
                R.drawable.ic_start
            }
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
    fun getStatus(): String {

        return when (obj.environmentStatus) {
            SshManager.Status.STARTING -> getString(R.string.starting)
            SshManager.Status.READY -> getString(R.string.ready)
            SshManager.Status.ERROR -> getString(R.string.error)
            SshManager.Status.IN_PROCESS -> getString(R.string.in_process)
            SshManager.Status.PENDING -> getString(R.string.pending)
        }
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.statusColor)
        notifyPropertyChanged(BR.statusIcon)
        notifyPropertyChanged(BR.status)
    }

    override fun getLocaleContext(): Context {
        return VpnApplication.getInstance()
    }
}