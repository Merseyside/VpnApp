package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view

import android.Manifest
import android.content.*
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
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
import com.merseyside.merseyLib.AnimatorList
import com.merseyside.merseyLib.presentation.view.OnBackPressedListener
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.Approach
import com.merseyside.merseyLib.Axis
import com.merseyside.merseyLib.MainPoint
import com.merseyside.merseyLib.animator.AlphaAnimator
import com.merseyside.merseyLib.animator.TransitionAnimator
import com.merseyside.merseyLib.utils.PermissionManager
import com.merseyside.merseyLib.utils.serialization.deserialize
import com.merseyside.merseyLib.utils.serialization.serialize
import com.merseyside.merseyLib.utils.time.Millis
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VpnStatus
import java.io.File

class DropletFragment : BaseVpnFragment<FragmentDropletBinding, DropletViewModel>(), OnBackPressedListener {

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

        if (!PermissionManager.isPermissionsGranted(baseActivity, *permission)) {

            PermissionManager.requestPermissions(
                this,
                *permission,
                requestCode = PERMISSION_ACCESS_CODE
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

    override val changeConnectionObserver = Observer<Boolean> {
        Logger.log(this, it)
//        if (it) {
//            if (vpnService!!.server != null) {
//                val currentServer = vpnService!!.server as Server
//                if (currentServer.id != viewModel.server.id) {
//                    turnOffVpn()
//                }
//            }
//            vpnService!!.server = viewModel.server
//        } else {
//            val currentServer = vpnService!!.server as Server
//
//            if (currentServer == viewModel.server) {
//                turnOffVpn()
//
//                viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED)
//            }
//        }
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

        doLayout()

        viewModel.configFileLiveData.observe(viewLifecycleOwner, configFileObserver)
        viewModel.serverStatusEvent.observe(viewLifecycleOwner, serverStatus)
        viewModel.openConfigFile.observe(viewLifecycleOwner, openConfigObserver)
        viewModel.storagePermissionsErrorLiveEvent.observe(viewLifecycleOwner, storagePermissionError)
    }

    private fun doLayout() {
        if (arguments?.containsKey(SERVER_KEY) == true && !viewModel.isInitialized) {
            viewModel.setServer1(requireArguments().getString(SERVER_KEY)!!.deserialize())
        }

        binding.config.setOnClickListener {
            if (binding.expandedGroup.visibility == View.VISIBLE) {
                binding.expandedGroup.visibility = View.GONE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_up))
            } else {
                binding.expandedGroup.visibility = View.VISIBLE
                binding.expandableIcon.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_down))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.configFileLiveData.removeObserver(configFileObserver)
        viewModel.serverStatusEvent.removeObserver(serverStatus)
        viewModel.openConfigFile.removeObserver(openConfigObserver)
        viewModel.storagePermissionsErrorLiveEvent.removeObserver(storagePermissionError)
    }
//
//    override fun receiveStatus(intent: Intent) {
//        viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status")))
//    }

//    override val mConnection = object : ServiceConnection {
//
//        override fun onServiceConnected(className: ComponentName,
//                                        service: IBinder
//        ) {
//            val binder = service as OpenVPNService.LocalBinder
//            vpnService = binder.service
//
//            Logger.log(this@DropletFragment, "on service connected")
//
//            if (VpnStatus.isVPNActive() && vpnService!!.server != null) {
//
//                val currentServer = vpnService!!.server as Server
//                if (currentServer.id == viewModel.server.id) {
//                    viewModel.setConnectionStatus(VpnStatus.ConnectionStatus.LEVEL_CONNECTED)
//                }
//            }
//        }

//        override fun onServiceDisconnected(arg0: ComponentName) {
//            //vpnService = null
//        }
//    }

    private fun shareOvpn(file: File) {

        Logger.log(this, "shareOvpn")

        val shareIntent = ShareCompat.IntentBuilder.from(baseActivity)
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
        viewModel.goBack()

        return false
    }

}