package com.merseyside.dropletapp.presentation.base

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.databinding.ViewDataBinding
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager

abstract class BaseVpnFragment<B : ViewDataBinding, M : BaseDropletViewModel> : BaseDropletFragment<B, M>() {


    protected var isServiceBind = false
    protected var vpnService: OpenVPNService? = null

    abstract val mConnection: ServiceConnection

    override fun onResume() {
        super.onResume()
        bindService()
    }

    override fun onPause() {
        super.onPause()
        unbindService()
    }

    private fun bindService() {
        val intent = Intent(baseActivity, OpenVPNService::class.java)
        intent.action = OpenVPNService.START_SERVICE
        isServiceBind = baseActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        if (isServiceBind) {
            isServiceBind = false
            baseActivity.unbindService(mConnection)
        }
    }

    protected fun turnOffVpn() {
        ProfileManager.setConnectedVpnProfileDisconnected(baseActivity)
        if (vpnService != null) {
            if (vpnService!!.management != null)
                vpnService!!.management.stopVPN(false)
            vpnService!!.server = null
        }
    }
}