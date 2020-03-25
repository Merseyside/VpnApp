package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.merseyside.admin.merseylibrary.system.PermissionsManager
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.databinding.FragmentDropletBinding
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.base.BaseVpnFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerDropletComponent
import com.merseyside.dropletapp.presentation.di.module.DropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model.DropletViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.mvvmcleanarch.data.serialization.deserialize
import com.merseyside.mvvmcleanarch.data.serialization.serialize
import com.merseyside.mvvmcleanarch.presentation.view.OnBackPressedListener
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.animation.AnimatorList
import com.merseyside.mvvmcleanarch.utils.animation.Approach
import com.merseyside.mvvmcleanarch.utils.animation.Axis
import com.merseyside.mvvmcleanarch.utils.animation.MainPoint
import com.merseyside.mvvmcleanarch.utils.animation.animator.AlphaAnimator
import com.merseyside.mvvmcleanarch.utils.animation.animator.TransitionAnimator
import com.merseyside.mvvmcleanarch.utils.time.Millis
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VPNLaunchHelper
import de.blinkt.openvpn.core.VpnStatus
import java.io.File

class DropletFragment : BaseVpnFragment<FragmentDropletBinding, DropletViewModel>(), OnBackPressedListener {

    private val vpnProfileObserver = Observer<VpnProfile> {
        if (it != null) {
            connectToVpn()
        }
    }

    private val configFileObserver = Observer<File> {
        shareOvpn(it)
    }

    private val openConfigObserver = Observer<File> {
        shareZip(it)
    }

    private val storagePermissionError = Observer<Any> {
        val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (!PermissionsManager.isPermissionsGranted(baseActivityView, permission)) {

            PermissionsManager.verifyStoragePermissions(this, permission,
                PERMISSION_ACCESS_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_ACCESS_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onConnect()
                } else {
                    showErrorMsg(getString(R.string.grant_permissions))
                }
            }
        }
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
                val currentServer = vpnService!!.server as Server
                if (currentServer.id != viewModel.server.id) {
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

        viewModel.vpnProfileLiveData.observe(viewLifecycleOwner, vpnProfileObserver)
        viewModel.connectionLiveData.observe(viewLifecycleOwner, changeConnectionObserver)
        viewModel.configFileLiveData.observe(viewLifecycleOwner, configFileObserver)
        viewModel.serverStatusEvent.observe(viewLifecycleOwner, serverStatus)
        viewModel.openConfigFile.observe(viewLifecycleOwner, openConfigObserver)
        viewModel.storagePermissionsErrorLiveEvent.observe(viewLifecycleOwner, storagePermissionError)
    }

    private fun doLayout() {
        if (arguments?.containsKey(SERVER_KEY) == true && !viewModel.isInitialized) {
            viewModel.setServer(arguments!!.getString(SERVER_KEY)!!.deserialize())
        }

        binding.config.setOnClickListener {
            if (binding.expandedGroup.visibility == View.VISIBLE) {
                binding.expandedGroup.visibility = View.GONE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivityView, R.drawable.ic_arrow_up))
            } else {
                binding.expandedGroup.visibility = View.VISIBLE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivityView, R.drawable.ic_arrow_down))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unregisterReceivers()

        viewModel.connectionLiveData.removeObserver(changeConnectionObserver)
        viewModel.vpnProfileLiveData.removeObserver(vpnProfileObserver)
        viewModel.configFileLiveData.removeObserver(configFileObserver)
        viewModel.serverStatusEvent.removeObserver(serverStatus)
        viewModel.openConfigFile.removeObserver(openConfigObserver)
        viewModel.storagePermissionsErrorLiveEvent.removeObserver(storagePermissionError)
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

                val currentServer = vpnService!!.server as Server
                Logger.log(this@DropletFragment, "show connected server")

                Logger.log(this@DropletFragment, vpnService!!.server!!)
                Logger.log(this@DropletFragment, viewModel.server)
                if (currentServer.id == viewModel.server.id) {
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

    private fun shareZip(file: File) {
        val newIntent = Intent(ACTION_VIEW)
        val mimeType = "zip"
        newIntent.setDataAndType(FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", file), mimeType)
        newIntent.flags = FLAG_ACTIVITY_NEW_TASK

        startActivity(newIntent)
    }

    private fun startAnimation() {
        val animation = AnimatorList(Approach.TOGETHER)

        animation.apply {
            addAnimator(
                TransitionAnimator(TransitionAnimator.Builder(
                    binding.configCard,
                    DURATION
                ).apply {
                    setInPercents(
                        1f to MainPoint.TOP_LEFT,
                        0f to MainPoint.TOP_LEFT,
                        axis = Axis.Y
                    )
                }
            ))

            addAnimator(
                AlphaAnimator(AlphaAnimator.Builder(
                    binding.configCard,
                    DURATION
                    ).apply {
                    values(0f, 1f)
                })
            )

            addAnimator(
                AlphaAnimator(AlphaAnimator.Builder(
                    binding.share,
                    DURATION
                ).apply {
                    values(0f, 1f)
                })
            )

            if (viewModel.server.typedConfig is TypedConfig.WireGuard) {

                addAnimator(
                    AlphaAnimator(AlphaAnimator.Builder(
                        binding.qr,
                        DURATION
                    ).apply {
                        values(0f, 1f)
                    })
                )
            }
        }

        animation.start()
    }

    companion object {
        private const val SERVER_KEY = "server"

        private const val START_VPN_PROFILE = 70
        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"

        private const val PERMISSION_ACCESS_CODE = 15

        private val DURATION = Millis(700)

        fun newInstance(server: Server): DropletFragment {
            val bundle = Bundle().apply {
                putString(SERVER_KEY, server.serialize())
            }

            return DropletFragment().also { it.arguments = bundle }
        }
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

}