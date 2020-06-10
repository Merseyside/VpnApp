package com.merseyside.dropletapp.presentation.base

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.connectionTypes.typeImpl.openVpn.OpenVpnConnectionType
import com.merseyside.merseyLib.utils.Logger
import de.blinkt.openvpn.core.VpnStatus

abstract class BaseVpnFragment<B : ViewDataBinding, M : BaseVpnViewModel> : BaseDropletFragment<B, M>() {

    private val prepareVpnObserver = Observer<Any> {
        prepareVpn()
    }

    protected abstract val changeConnectionObserver: Observer<Boolean>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.connectionLiveData.observe(viewLifecycleOwner, changeConnectionObserver)
        viewModel.vpnNotPreparedLiveEvent.observe(viewLifecycleOwner, prepareVpnObserver)
    }

    override fun onResume() {
        super.onResume()
        baseActivity.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.connectionLiveData.removeObserver(changeConnectionObserver)
        viewModel.vpnNotPreparedLiveEvent.removeObserver(prepareVpnObserver)
    }

    private fun prepareVpn() {
        val intent = OpenVpnConnectionType.prepare(context)

        if (intent != null) {
            VpnStatus.updateStateString(
                "USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT
            )

            try {
                startActivityForResult(intent, START_VPN_PROFILE)
            } catch (e: ActivityNotFoundException) {
                VpnStatus.logError(R.string.no_vpn_support_image)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                START_VPN_PROFILE -> {
                    viewModel.onConnect()
                }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Logger.log(this, "onBack")
        }

    }

    companion object {
        private const val START_VPN_PROFILE = 71
        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"
    }
}