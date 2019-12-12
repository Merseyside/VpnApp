package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
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
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.adapter.BaseAdapter
import com.upstream.basemvvmimpl.presentation.adapter.UpdateRequest
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import java.io.File

class DropletListFragment : BaseDropletFragment<FragmentDropletListBinding, DropletListViewModel>() {

    private val adapter: DropletAdapter = DropletAdapter()

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

    private val dropletObserver = Observer<List<Server>> {

        if (it.isEmpty()) {
            adapter.removeAll()
        } else {
            if (adapter.isEmpty()) {

                adapter.add(it)
            } else {
                adapter.update(UpdateRequest.Builder<Server>()
                    .isAddNew(true)
                    .isDeleteOld(true)
                    .setList(it)
                    .build()
                )
            }
        }
    }

    private val changeConnectionObserver = Observer<Server?> {
        if (it != null) {
            adapter.notifyItemChanged(it)

            if (it.connectStatus) {
                vpnService!!.server = it
            } else {
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

        viewModel.dropletLiveData.observe(this, dropletObserver)
        viewModel.vpnProfileLiveData.observe(this, vpnProfileObserver)
        viewModel.connectionLiveData.observe(this, changeConnectionObserver)
        viewModel.ovpnFileLiveData.observe(this, ovpnFileObserver)
    }

    private fun init() {
        adapter.setOnItemOptionClickListener(object: DropletAdapter.OnItemOptionsClickListener {
            override fun onShareOvpn(server: Server) {
                viewModel.shareOvpnFile(server)
            }

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

        adapter.setOnShareClickListener(object: DropletItemViewModel.OnShareClickListener {
            override fun onShareOvpn(server: Server) {
                viewModel.shareOvpnFile(server)
            }
        })
    }

    private fun connectToVpn(server: Server) {
        if (server.environmentStatus == SshManager.Status.ERROR) {
            showAlertDialog(
                messageRes = R.string.error_dialog_message,
                positiveButtonTextRes = R.string.error_dialog_positive,
                negativeButtonTextRes = R.string.error_dialog_negative,
                onPositiveClick = { viewModel.deleteServer(server) }
            )
        } else {

            viewModel.onServerClick(server)
        }
    }

    private fun doLayout() {
        adapter.setOnItemClickListener(onServerClickListener)

        binding.serverList.adapter = adapter
    }

    private val onServerClickListener = object: BaseAdapter.OnItemClickListener<Server> {
        override fun onItemClicked(obj: Server) {
            if (obj.environmentStatus == SshManager.Status.PENDING) {
                viewModel.prepareServer(obj)
            } else {
                connectToVpn(obj)
            }
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

        adapter.removeOnItemClickListener(onServerClickListener)

        unregisterReceivers()

        viewModel.connectionLiveData.removeObserver(changeConnectionObserver)
        viewModel.vpnProfileLiveData.removeObserver(vpnProfileObserver)
        viewModel.dropletLiveData.removeObserver(dropletObserver)
        viewModel.ovpnFileLiveData.removeObserver(ovpnFileObserver)

        viewModel.vpnProfileLiveData.value = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

            if (VpnStatus.isVPNActive() && vpnService!!.server != null) {
                Log.d(TAG, "show connected server")
                viewModel.showConnectedServer(vpnService!!.server as Server)
            }
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

    private fun shareOvpn(file: File) {

        Log.d(TAG, "shareOvpn")

        val shareIntent = ShareCompat.IntentBuilder.from(baseActivityView)
            .setType("text/*")
            .setStream(FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", file))
            .setChooserTitle(R.string.share_ovpn_to)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(shareIntent)
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