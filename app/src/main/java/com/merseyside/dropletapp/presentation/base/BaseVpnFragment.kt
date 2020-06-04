package com.merseyside.dropletapp.presentation.base

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view.DropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.free.view.FreeAccessFragment
import com.merseyside.merseyLib.utils.Logger
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus

abstract class BaseVpnFragment<B : ViewDataBinding, M : BaseVpnViewModel> : BaseDropletFragment<B, M>() {

    private val vpnProfileObserver = Observer<VpnProfile> {
        if (it != null) {
            connectToVpn()
        }
    }

    protected abstract val changeConnectionObserver: Observer<Boolean>

    protected var isServiceBind = false
    protected var vpnService: OpenVPNService? = null

    abstract val mConnection: ServiceConnection

    abstract fun receiveStatus(intent: Intent)

    private var br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            receiveStatus(intent)
        }
    }

    private fun registerReceivers() {
        baseActivity.registerReceiver(br, IntentFilter(BROADCAST_ACTION))
    }

    private fun unregisterReceivers() {
        baseActivity.unregisterReceiver(br)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerReceivers()

        viewModel.connectionLiveData.observe(viewLifecycleOwner, changeConnectionObserver)
        viewModel.vpnProfileLiveData.observe(viewLifecycleOwner, vpnProfileObserver)
    }

    override fun onResume() {
        super.onResume()
        baseActivity.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        bindService()
    }

    override fun onPause() {
        super.onPause()
        unbindService()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unregisterReceivers()

        viewModel.connectionLiveData.removeObserver(changeConnectionObserver)
        viewModel.vpnProfileLiveData.removeObserver(vpnProfileObserver)
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

    private fun connectToVpn() {
        val intent = VpnService.prepare(baseActivity)

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT)

            try {
                startActivityForResult(intent, START_VPN_PROFILE)
            } catch (e: ActivityNotFoundException) {
                VpnStatus.logError(R.string.no_vpn_support_image)
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_VPN_PROFILE -> {
                    vpnService!!.server = viewModel.server
                    VPNLaunchHelper.startOpenVpn(viewModel.vpnProfileLiveData.value, context)

                    viewModel.sessionTime = vpnService!!.sessionTime
                }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Logger.log(this, "onBack")
        }

    }

    protected fun getConnectionTime(): Long {
        return vpnService?.sessionTime ?: 0
    }

    companion object {
        private const val START_VPN_PROFILE = 70
        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"
    }
}