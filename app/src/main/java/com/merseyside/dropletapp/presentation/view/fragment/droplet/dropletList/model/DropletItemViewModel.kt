package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

class DropletItemViewModel(private var server: Server) : BaseComparableAdapterViewModel<Server>() {

    override fun isContentTheSame(obj: Server): Boolean {
        return server == obj
    }

    override fun isItemsTheSame(obj: Server): Boolean {
        return server == obj
    }

    override fun compareTo(obj: Server): Int {
        return obj.id.compareTo(server.id)
    }

    override fun getItem(): Server {
        return server
    }

    override fun setItem(item: Server) {
        this.server = item
    }

    fun onClick() {
        getClickListener()?.onItemClicked(server)
    }

    @Bindable
    fun getServerName(): String {
        return "Server: ${server.name}"
    }

    @Bindable
    fun getProviderName(): String {
        return "Provider: ${server.providerName}"
    }

    @Bindable
    fun getRegion(): String {
        return "Region: ${server.regionName}"
    }

    @Bindable
    fun getStatus(): String {
        return server.environmentStatus.toString()
    }

    @Bindable
    fun getStatusColor(): Int {
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