package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.admin.merseylibrary.system.FileSystemHelper
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.mvvmcleanarch.utils.SingleLiveEvent
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.UpstreamConfigParser
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import java.io.File
import kotlin.coroutines.CoroutineContext

class DropletViewModel(
    router: Router,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val createServerUseCase: CreateServerInteractor,
    private val deleteServerUseCase: DeleteDropletInteractor
) : BaseDropletViewModel(router), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    val serverStatusEvent = SingleLiveEvent<SshManager.Status>()

    val providerIcon = ObservableField<Int>()
    val providerTitle = ObservableField<String>()

    val statusIcon = ObservableField<Int>()
    val statusColor = ObservableField<Int>()
    val status = ObservableField<String>()

    val region = ObservableField<String>()
    val address = ObservableField<String>()
    val type = ObservableField<String>()

    val connectButtonColor = ObservableField<Int>()
    val connectButtonTitle = ObservableField<String>(getString(R.string.connect))
    val isConnectButtonEnable = ObservableField<Boolean>(true)

    val connectionLiveData = SingleLiveEvent<Boolean>()
    val ovpnFileLiveData = MutableLiveData<File>()
    val vpnProfileLiveData = MutableLiveData<VpnProfile>()

    val serverConfigTitle = ObservableField<String>(getString(R.string.server_config))
    val serverConfig = ObservableField<String>()

    lateinit var server: Server
        private set

    private var isConnected = false
    set(value) {
        if (field != value) {
            field = value

            connectionLiveData.value = value
        }
    }

    private var loadServersJob: Job? = null

    @UseExperimental(InternalCoroutinesApi::class)
    private fun loadServers(): Job {
        return launch {
            getDropletsUseCase.observe().collect(dropletObserver)
        }
    }

    private val dropletObserver = object : FlowCollector<List<Server>> {
        override suspend fun emit(value: List<Server>) {

            value.forEach {
                if (it.id == server.id) {
                    setServer(it)
                    return
                }
            }
        }
    }

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getDropletsUseCase.cancel()
        deleteServerUseCase.cancel()
        createServerUseCase.cancel()
    }

    override fun updateLanguage(context: Context) {
        region.set(context.getString(R.string.region, server.regionName))
        address.set(context.getString(R.string.address, server.address))

        serverConfigTitle.set(context.getString(R.string.server_config))
        //type.set()
    }

    fun onConnect() {
        if (server.environmentStatus == SshManager.Status.READY) {
            if (!isConnected) {
                prepareVpn(server.config!!)
            } else {
                isConnected = false
            }
        } else {
            when(server.environmentStatus) {
                SshManager.Status.ERROR -> {
                    deleteServer(server)
                }

                SshManager.Status.PENDING -> {
                    prepareServer(server)
                }

                else -> {}
            }
        }
    }

    private fun loadVpnProfile(body: String): VpnProfile? {
        return UpstreamConfigParser.parseConfig(VpnApplication.getInstance(), body)
    }

    private fun prepareVpn(body: String) {
        val vpnProfile: VpnProfile? = loadVpnProfile(body)
        if (vpnProfile != null) {
            vpnProfileLiveData.value = vpnProfile
        }
    }

    fun setConnectionStatus(status: VpnStatus.ConnectionStatus) {
        updateConnectButtonWithVpnStatus(status)

        when (status) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                if (isConnected) return
                this.isConnected = true
            }

            else -> {
                if (!this.isConnected) return
                this.isConnected = false
            }
        }
    }

    private fun prepareServer(server: Server) {
        createServerUseCase.execute(
            params = CreateServerInteractor.Params(
                dropletId = server.id,
                providerId = server.providerId,
                logCallback = object: ProviderRepositoryImpl.LogCallback {
                    override fun onLog(log: String) {
                        Handler(Looper.getMainLooper()).post {
                            showProgress(log)
                        }
                    }
                }),
            onComplete = {
                loadServersJob = loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            hideProgress = { hideProgress() }
        )
    }

    private fun deleteServer(server: Server) {
        deleteServerUseCase.execute(
            params = DeleteDropletInteractor.Params(server.providerId, server.id),
            onComplete = {
                goBack()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            showProgress = { showProgress() },
            hideProgress = { hideProgress() }
        )
    }

    fun shareOvpnFile() {
        ovpnFileLiveData.value = FileSystemHelper.createTempFile(server.name, ".ovpn", server.config!!)
    }

    fun setServer(server: Server) {
        this.server = server

        serverStatusEvent.value = server.environmentStatus

        updateConnectButtonWithServerStatus(server.environmentStatus)

        providerIcon.set(getIcon())
        providerTitle.set(getTitle())

        statusIcon.set(getStatusIcon())
        statusColor.set(getStatusColor())
        status.set(getStatus())

        if (server.environmentStatus == SshManager.Status.READY && loadServersJob != null) {
            loadServersJob?.cancel()
            loadServersJob = null
        }

        serverConfig.set(server.config)

        updateLanguage(VpnApplication.getInstance().context)
    }

    private fun getStatus(): String {

        return when (server.environmentStatus) {
            SshManager.Status.READY -> VpnApplication.getInstance().getActualString(R.string.ready)
            SshManager.Status.ERROR -> VpnApplication.getInstance().getActualString(R.string.error)
            SshManager.Status.IN_PROCESS -> VpnApplication.getInstance().getActualString(R.string.in_process)
            SshManager.Status.PENDING -> VpnApplication.getInstance().getActualString(R.string.pending)
        }
    }

    private fun getStatusColor(): Int {

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
            SshManager.Status.ERROR -> {
                R.attr.colorError
            }
        }
    }

    @DrawableRes
    fun getStatusIcon(): Int {

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
            SshManager.Status.ERROR -> {
                R.drawable.ic_error
            }
        }
    }

    @DrawableRes
    fun getIcon(): Int {
        return when(Provider.getProviderById(server.providerId)) {
            is Provider.DigitalOcean -> R.drawable.digital_ocean
            is Provider.Linode -> R.drawable.ic_linode
            is Provider.CryptoServers -> R.drawable.crypto_servers
            null -> TODO()
        }
    }

    private fun updateConnectButtonWithVpnStatus(status: VpnStatus.ConnectionStatus) {
        connectButtonColor.set(getConnectButtonColor(status))
        connectButtonTitle.set(getConnectButtonTitle(status))
    }

    private fun updateConnectButtonWithServerStatus(status: SshManager.Status) {
        isConnectButtonEnable.set(getConnectButtonAvailability(status))
        connectButtonColor.set(getConnectButtonColor(status))
        connectButtonTitle.set(getConnectButtonTitle(status))
    }

    @DrawableRes
    private fun getConnectButtonColor(status: VpnStatus.ConnectionStatus): Int {
        return when(status) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                R.attr.colorError
            }

            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED -> {
                R.attr.colorPrimary
            }

            else -> {
                R.attr.pendingColor
            }
        }
    }

    private fun getConnectButtonTitle(serverStatus: VpnStatus.ConnectionStatus): String {
        return when(serverStatus) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                getString(R.string.disconnect_action)
            }

            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED -> {
                getString(R.string.connect)
            }

            else -> {
                getString(R.string.connecting)
            }
        }
    }

    @DrawableRes
    private fun getConnectButtonColor(status: SshManager.Status): Int {
        return when(status) {
            SshManager.Status.ERROR -> {
                R.attr.colorError
            }

            SshManager.Status.IN_PROCESS -> {
                R.attr.inactiveColor
            }

            SshManager.Status.PENDING -> {
                R.attr.pendingColor
            }

            SshManager.Status.READY -> {
                R.attr.colorPrimary
            }
        }
    }

    private fun getConnectButtonTitle(status: SshManager.Status): String {
        return when(status) {
            SshManager.Status.ERROR -> {
                getString(R.string.delete_action)
            }

            SshManager.Status.IN_PROCESS -> {
                getString(R.string.setting_up)
            }

            SshManager.Status.PENDING -> {
                getString(R.string.setup_server)
            }

            SshManager.Status.READY -> {
                getString(R.string.connect)
            }
        }
    }

    private fun getConnectButtonAvailability(status: SshManager.Status): Boolean {
        return when(status) {
            SshManager.Status.IN_PROCESS -> {
                false
            }

            else -> {
                true
            }

        }
    }

    fun getTitle(): String {
        return server.providerName
    }
}