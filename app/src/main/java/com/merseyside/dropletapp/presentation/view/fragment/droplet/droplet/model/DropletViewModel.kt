package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.admin.merseylibrary.data.filemanager.FileManager
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.exception.BannedAddressException
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.utils.getLogByStatus
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.SingleLiveEvent
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.UpstreamConfigParser
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import java.io.File
import java.lang.IllegalStateException
import kotlin.coroutines.CoroutineContext

class DropletViewModel(
    private val router: Router,
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
    val isConnectButtonVisible = ObservableField<Boolean>(true)

    val isQrVisible = ObservableField<Boolean>(false)
    val qrTitleText = ObservableField<String>(getString(R.string.show_qr))

    val connectionLiveData = SingleLiveEvent<Boolean>()
    val configFileLiveData = SingleLiveEvent<File>()
    val vpnProfileLiveData = MutableLiveData<VpnProfile>()
    val openConfigFile = SingleLiveEvent<File>()
    val storagePermissionsErrorLiveEvent = SingleLiveEvent<Any>()

    val serverConfigTitle = ObservableField<String>(getString(R.string.server_config))
    val serverConfig = ObservableField<String>()

    lateinit var server: Server
        private set

    var isInitialized: Boolean = false
    get() {
        return try {
            server.id
            true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

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
        type.set(context.getString(R.string.type, server.typedConfig.getName()))

        serverConfigTitle.set(context.getString(R.string.server_config))
        qrTitleText.set(context.getString(R.string.show_qr))
    }

    fun onConnect() {
        if (server.typedConfig is TypedConfig.WireGuard && server.environmentStatus == SshManager.Status.READY) {
            val externalStorage = FileManager.getStorageLocations(FileManager.STORAGE.SD_CARD)

            if (externalStorage != null) {
                val resultFile = FileManager.createFile("${externalStorage.path}/MyVpn", "${server.name}.conf", server.getConfig()!!)

                if (resultFile != null) {
                    showAlertDialog(
                        title = getString(R.string.wireguard_title),
                        message = getString(R.string.wireguard_description, resultFile.path),
                        positiveButtonText = getString(R.string.wireguard_positive_text),
                        isOneAction = true
                    )
                } else {
                    storagePermissionsErrorLiveEvent.call()
                }
            }

        } else {
            if (server.environmentStatus == SshManager.Status.READY) {
                if (!isConnected) {
                    prepareVpn(server.typedConfig.config!!)
                } else {
                    isConnected = false
                }
            } else {
                when (server.environmentStatus) {
                    SshManager.Status.ERROR -> {
                        deleteServer()
                    }
                    SshManager.Status.PENDING -> {
                        prepareServer()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun onQrClick() {
        if (server.getConfig() != null) {
            navigateToQrScreen(server.getConfig()!!)
        }
    }

    private fun navigateToQrScreen(config: String) {
        router.navigateTo(Screens.QrScreen(config))
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

    private fun prepareServer() {
        createServerUseCase.execute(
            params = CreateServerInteractor.Params(
                dropletId = server.id,
                providerId = server.providerId,
                logCallback = object: ProviderRepositoryImpl.LogCallback {
                    override fun onLog(log: ProviderRepositoryImpl.LogStatus) {
                        Handler(Looper.getMainLooper()).post {
                            showProgress(getLogByStatus(VpnApplication.getInstance().context, log))
                        }
                    }
                }),
            onPreExecute = {
                isNavigationEnable = false
            },
            onComplete = {
                loadServersJob = loadServers()
            },
            onError = { throwable ->
                if (throwable is BannedAddressException) {
                    goBack()
                }
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            onPostExecute = {
                hideProgress()
                isNavigationEnable = true
            }
        )
    }

    private fun deleteServer() {
        deleteServerUseCase.execute(
            params = DeleteDropletInteractor.Params(server.providerId, server.id),
            onComplete = {
                goBack()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            onPreExecute = { showProgress() },
            onPostExecute = { hideProgress() }
        )
    }

    fun shareConfigFile() {
        configFileLiveData.value = when(server.typedConfig) {
            is TypedConfig.OpenVpn -> {
                FileManager.createTempFile(server.name, ".ovpn", server.typedConfig.config!!)
            }

            is TypedConfig.WireGuard -> {
                FileManager.createTempFile(server.name, ".conf", server.typedConfig.config!!)
            }

            else -> {
                FileManager.createTempFile(server.name, ".txt", server.typedConfig.config!!)
            }
        }

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
        } else if (server.environmentStatus == SshManager.Status.IN_PROCESS && loadServersJob == null) {
            loadServersJob = loadServers()
        } else if (server.environmentStatus == SshManager.Status.ERROR) {
            Logger.log(this, "here")
            showAlertDialog(
                context = VpnApplication.getInstance().context,
                titleRes = R.string.delete_server,
                messageRes = R.string.banned_msg,
                positiveButtonTextRes = R.string.delete_action,
                negativeButtonTextRes = R.string.error_dialog_negative,
                onPositiveClick = {
                    deleteServer()
                }
            )
        }

        if (server.environmentStatus == SshManager.Status.READY) {
            if (server.typedConfig is TypedConfig.PPTP || server.typedConfig is TypedConfig.L2TP) {
                isConnectButtonVisible.set(false)
            }
        }

        if (server.typedConfig is TypedConfig.WireGuard && server.environmentStatus == SshManager.Status.READY) {
            isQrVisible.set(true)
        } else {
            isQrVisible.set(false)
        }

        serverConfig.set(server.getConfig())

        updateLanguage(VpnApplication.getInstance().context)
    }

    private fun getStatus(): String {

        return when (server.environmentStatus) {
            SshManager.Status.STARTING -> VpnApplication.getInstance().getActualString(R.string.starting)
            SshManager.Status.READY -> VpnApplication.getInstance().getActualString(R.string.ready)
            SshManager.Status.ERROR -> VpnApplication.getInstance().getActualString(R.string.error)
            SshManager.Status.IN_PROCESS -> VpnApplication.getInstance().getActualString(R.string.in_process)
            SshManager.Status.PENDING -> VpnApplication.getInstance().getActualString(R.string.pending)
        }
    }

    private fun getStatusColor(): Int {

        return when(server.environmentStatus) {
            SshManager.Status.STARTING -> {
                R.attr.colorPrimary
            }
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
            SshManager.Status.STARTING -> {
                R.drawable.ic_start
            }
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
            else -> { throw IllegalStateException() }
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
                if (server.typedConfig is TypedConfig.WireGuard) {
                    getString(R.string.save_config)
                } else {
                    getString(R.string.connect)
                }
            }
            else -> { throw IllegalStateException() }
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

    override fun onBackPressed(): Boolean {
        return isNavigationEnable
    }
}