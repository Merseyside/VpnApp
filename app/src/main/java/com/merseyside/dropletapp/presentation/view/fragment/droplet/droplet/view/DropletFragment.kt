package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentDropletBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletComponent
import com.merseyside.dropletapp.presentation.di.module.DropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model.DropletViewModel
import com.merseyside.mvvmcleanarch.data.deserialize
import com.merseyside.mvvmcleanarch.data.serialize
import com.merseyside.mvvmcleanarch.utils.Logger
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import java.io.File

class DropletFragment : BaseDropletFragment<FragmentDropletBinding, DropletViewModel>() {

    private var isServiceBind = false
    private var vpnService: OpenVPNService? = null

    private val vpnProfileObserver = Observer<VpnProfile> {
        if (it != null) {
            connectToVpn()
        }
    }

    private val ovpnFileObserver = Observer<File> {
        shareOvpn(it)
    }

    private fun connectToVpn() {
        val intent = VpnService.prepare(baseActivityView)

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

    private val changeConnectionObserver = Observer<Boolean> {
        if (it) {
            if (vpnService!!.server != null) {
                if (vpnService!!.server != viewModel.server) {
                    turnOffVpn()
                }
            }
            vpnService!!.server = it
        } else {
            if (vpnService!!.server == viewModel.server) {
                vpnService!!.server = null
                turnOffVpn()
            }
        }
    }

    private fun turnOffVpn() {
        ProfileManager.setConntectedVpnProfileDisconnected(baseActivityView)
        if (vpnService != null) {
            if (vpnService!!.management != null)
                vpnService!!.management.stopVPN(false)
            vpnService!!.server = null
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerDropletComponent.builder()
            .appComponent(getAppComponent())
            .dropletModule(getDropletModule(bundle))
            .build().inject(this)
    }

    private fun getDropletModule(bundle: Bundle?): DropletModule {
        return DropletModule(this, bundle)
    }

    override fun loadingObserver(isLoading: Boolean) {}

    override fun getLayoutId(): Int {
        return R.layout.fragment_droplet
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.nav_server)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerReceivers()

        doLayout()

        viewModel.vpnProfileLiveData.observe(this, vpnProfileObserver)
        viewModel.connectionLiveData.observe(this, changeConnectionObserver)
        viewModel.ovpnFileLiveData.observe(this, ovpnFileObserver)
    }

    private fun doLayout() {
        if (arguments?.containsKey(SERVER_KEY) == true) {
            viewModel.setServer(arguments!!.getString(SERVER_KEY)!!.deserialize())
        }
    }

    override fun onResume() {
        super.onResume()
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
        viewModel.ovpnFileLiveData.removeObserver(ovpnFileObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_VPN_PROFILE -> {
                    VPNLaunchHelper.startOpenVpn(viewModel.vpnProfileLiveData.value, context)
                }
            }
        }
    }

    private fun bindService() {
        val intent = Intent(baseActivityView, OpenVPNService::class.java)
        intent.action = OpenVPNService.START_SERVICE
        isServiceBind = baseActivityView.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        if (isServiceBind) {
            isServiceBind = false
            baseActivityView.unbindService(mConnection)
        }
    }

    private var br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            receiveStatus(intent)
        }
    }

    private fun registerReceivers() {
        baseActivityView.registerReceiver(br, IntentFilter(BROADCAST_ACTION))
    }

    private fun unregisterReceivers() {
        baseActivityView.unregisterReceiver(br)
    }

    private fun receiveStatus(intent: Intent) {
        viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status")))
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as OpenVPNService.LocalBinder
            vpnService = binder.service

            if (VpnStatus.isVPNActive() && vpnService!!.server != null) {
                Logger.log(this, "show connected server")

                viewModel.setConnectionStatus(vpnService!!.server == viewModel.server)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            vpnService = null
        }
    }

    private fun shareOvpn(file: File) {

        Logger.log(this, "shareOvpn")

        val shareIntent = ShareCompat.IntentBuilder.from(baseActivityView)
            .setType("text/*")
            .setStream(FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", file))
            .setChooserTitle(R.string.share_ovpn_to)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(shareIntent)
    }

    companion object {
        private const val SERVER_KEY = "server"

        private const val START_VPN_PROFILE = 70
        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"

        fun newInstance(server: Server): DropletFragment {
            val bundle = Bundle().apply {
                putString(SERVER_KEY, server.serialize())
            }

            return DropletFragment().also { it.arguments = bundle }
        }
    }
}