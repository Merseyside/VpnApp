package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletListViewModel
import com.merseyside.dropletapp.databinding.FragmentDropletListBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletListComponent
import com.merseyside.dropletapp.presentation.di.module.DropletListModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter.DropletAdapter
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.adapter.BaseAdapter
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus

class DropletListFragment : BaseDropletFragment<FragmentDropletListBinding, DropletListViewModel>() {

    private lateinit var adapter: DropletAdapter

    private var isServiceBind = false
    private var vpnService: OpenVPNService? = null

    private val vpnProfileObserver = Observer<VpnProfile> {
        connectToVpn()
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

    private val dropletObserver = Observer<List<Server>> {
        Log.d(TAG, "here")

        if (!adapter.hasItems()) {
            adapter.add(it)
        } else {
            adapter.update(it)
        }
    }

    override fun setBindingVariable(): Int {
        return BR.viewModel
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerDropletListComponent.builder()
            .appComponent(getAppComponent())
            .dropletListModule(getDropletListModule(bundle))
            .build().inject(this)
    }

    private fun getDropletListModule(bundle: Bundle?): DropletListModule {
        return DropletListModule(this, bundle)
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_droplet_list
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun getTitle(context: Context): String? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerReceivers()
        doLayout()
    }

    private fun init() {

        adapter = DropletAdapter()
        adapter.setOnItemOptionClickListener(object: DropletAdapter.OnItemOptionsClickListener {
            override fun onPrepare(server: Server) {
                viewModel.prepareServer(server)
            }

            override fun onConnect(server: Server) {
                connectToVpn(server)
            }

            override fun onDelete(server: Server) {
                viewModel.deleteServer(server)
            }

        })

        adapter.setOnItemClickListener(object: BaseAdapter.AdapterClickListener {
            override fun onItemClicked(obj: Any) {
                if (obj is Server) {
                    if (obj.environmentStatus == SshManager.Status.PENDING) {
                        viewModel.prepareServer(obj)
                    } else {
                        connectToVpn(obj)
                    }
                }
            }

        })

        viewModel.dropletLiveData.observe(this, dropletObserver)
        viewModel.vpnProfileLiveData.observe(this, vpnProfileObserver)
    }

    private fun connectToVpn(server: Server) {

        if (viewModel.isConnected()) {
            ProfileManager.setConntectedVpnProfileDisconnected(baseActivityView)
            if (vpnService != null) {
                if (vpnService!!.management != null)
                    vpnService!!.management.stopVPN(false)
                vpnService!!.currentServer = null
            }
        }

        viewModel.getOvpnFile(server)
    }

    private fun doLayout() {
        binding.serverList.adapter = adapter

        binding.fab.setOnClickListener {
            viewModel.navigateToAddDropletScreen()
        }

        viewModel.loadServers()

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult $requestCode $resultCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_VPN_PROFILE -> {
                    VPNLaunchHelper.startOpenVpn(viewModel.vpnProfileLiveData.value, context)
                }
                ADBLOCK_REQUEST -> {
                    Log.d(TAG, "onActivityResult($requestCode,$resultCode,$data")
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

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as OpenVPNService.LocalBinder
            vpnService = binder.service

//            if (VpnStatus.isVPNActive() && vpnService!!.currentServer != null) {
//                viewModel.showConnectedServer(vpnService!!.currentServer as Server, vpnService!!.connectTime)
//            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            vpnService = null
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

    companion object {
        private const val START_VPN_PROFILE = 70
        private const val ADBLOCK_REQUEST = 10001

        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"

        private const val TAG = "DropletListFragment"

        fun newInstance(): DropletListFragment {
            return DropletListFragment()
        }
    }
}