package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentDropletBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.base.BaseVpnFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletComponent
import com.merseyside.dropletapp.presentation.di.module.DropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model.DropletViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.mvvmcleanarch.data.deserialize
import com.merseyside.mvvmcleanarch.data.serialize
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.ValueAnimatorHelper
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import java.io.File

class DropletFragment : BaseVpnFragment<FragmentDropletBinding, DropletViewModel>() {


    private val vpnProfileObserver = Observer<VpnProfile> {
        if (it != null) {
            connectToVpn()
        }
    }

    private val ovpnFileObserver = Observer<File> {
        shareOvpn(it)
    }

    private val serverStatus = Observer<SshManager.Status> {
        if (it != SshManager.Status.READY) {
            binding.share.visibility = View.GONE
            binding.configCard.visibility = View.GONE
        } else if (it == SshManager.Status.READY) {
            if (binding.share.visibility == View.GONE) {
                startAnimation()
            }
        }
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
        Logger.log(this, it)
        if (it) {
            if (vpnService!!.server != null) {
                if (vpnService!!.server != viewModel.server) {
                    turnOffVpn()
                }
            }
            vpnService!!.server = viewModel.server
        } else {
            val currentServer = vpnService!!.server as Server

            if (currentServer == viewModel.server) {
                vpnService!!.server = null
                turnOffVpn()

                viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED)
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerReceivers()

        doLayout()

        viewModel.vpnProfileLiveData.observe(this, vpnProfileObserver)
        viewModel.connectionLiveData.observe(this, changeConnectionObserver)
        viewModel.ovpnFileLiveData.observe(this, ovpnFileObserver)
        viewModel.serverStatusEvent.observe(this, serverStatus)
    }

    private fun doLayout() {
        if (arguments?.containsKey(SERVER_KEY) == true) {
            viewModel.setServer(arguments!!.getString(SERVER_KEY)!!.deserialize())
        }

        binding.config.setOnClickListener {
            if (binding.expandedGroup.visibility == View.VISIBLE) {
                binding.expandedGroup.visibility = View.GONE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivityView, R.drawable.ic_arrow_down))
            } else {
                binding.expandedGroup.visibility = View.VISIBLE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivityView, R.drawable.ic_arrow_up))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unregisterReceivers()

        viewModel.connectionLiveData.removeObserver(changeConnectionObserver)
        viewModel.vpnProfileLiveData.removeObserver(vpnProfileObserver)
        viewModel.ovpnFileLiveData.removeObserver(ovpnFileObserver)
        viewModel.serverStatusEvent.removeObserver(serverStatus)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                START_VPN_PROFILE -> {
                    vpnService!!.server = viewModel.server
                    VPNLaunchHelper.startOpenVpn(viewModel.vpnProfileLiveData.value, context)
                }
            }
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

    override val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as OpenVPNService.LocalBinder
            vpnService = binder.service

            Logger.log(this@DropletFragment, "on service connected")

            if (VpnStatus.isVPNActive() && vpnService!!.server != null) {
                Logger.log(this@DropletFragment, "show connected server")

                if (vpnService!!.server == viewModel.server) {
                    viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.LEVEL_CONNECTED)
                }
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

    private fun startAnimation() {
        val animation = ValueAnimatorHelper()

        animation.addAnimation(
            ValueAnimatorHelper.Builder(binding.configCard)
                .translateAnimationPercent(
                    percents  = *floatArrayOf(1f, 0f),
                    mainPoint = ValueAnimatorHelper.MainPoint.TOP_LEFT,
                    animAxis  = ValueAnimatorHelper.AnimAxis.Y_AXIS,
                    duration  = 700
                ).build()
        )

        animation.addAnimation(
            ValueAnimatorHelper.Builder(binding.configCard)
                .alphaAnimation(
                    floats = *floatArrayOf(0f, 1f),
                    duration = 700
                ).build()
        )

        animation.addAnimation(
            ValueAnimatorHelper.Builder(binding.share)
                .alphaAnimation(
                    floats = *floatArrayOf(0f, 1f),
                    duration = 700
                ).build()
        )

        animation.playTogether()
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