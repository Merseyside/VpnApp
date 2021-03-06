package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import com.github.shadowsocks.plugin.PluginManager
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.connectionTypes.*
import com.merseyside.dropletapp.connectionTypes.typeImpl.openVpn.OpenVpnConnectionType
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.exception.BannedAddressException
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseVpnViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.utils.application
import com.merseyside.dropletapp.utils.getLogByStatus
import com.merseyside.dropletapp.utils.getProviderIcon
import com.merseyside.filemanager.FileManager
import com.merseyside.merseyLib.utils.ext.log
import com.merseyside.merseyLib.utils.mainThread
import com.merseyside.merseyLib.utils.mvvm.SingleLiveEvent
import kotlinx.coroutines.*
import ru.terrakok.cicerone.Router
import java.io.File
import kotlin.coroutines.CoroutineContext

class DropletViewModel(
    private val router: Router,
    private val connectionTypeBuilder: Builder,
    private val getDropletsUseCase: GetDropletsInteractor,
    private val createServerUseCase: CreateServerInteractor,
    private val deleteServerUseCase: DeleteDropletInteractor
) : BaseVpnViewModel(router), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    private var connectionType: ServiceConnectionType? = null
        set(value) {
            field = value

            value?.apply {

                setConnectionCallback(object : ConnectionCallback {
                    override fun onConnectionEvent(connectionLevel: ConnectionLevel) {
                        connectionLevel.log()
                        setConnectionStatus(connectionLevel)
                    }

                    override fun onSessionTime(timestamp: Long) {}
                })

                start(this@DropletViewModel.server)
            }
        }


    val serverStatusEvent = SingleLiveEvent<SshManager.Status>()
    val v2RayRequireEvent = SingleLiveEvent<Any>()

    val providerIcon = ObservableField<Int>()
    val providerTitle = ObservableField<String>()

    val statusIcon = ObservableField<Int>()
    val statusColor = ObservableField<Int>()
    val status = ObservableField<String>()

    val region = ObservableField<String>()
    val address = ObservableField<String>()
    val type = ObservableField<String>()

    val connectButtonColor = ObservableField<Int>()
    val connectButtonTitle = ObservableField(getString(R.string.connect))
    val isConnectButtonEnable = ObservableField(true)
    val isConnectButtonVisible = ObservableField(true)

    val isQrVisible = ObservableField(false)
    val qrTitleText = ObservableField(getString(R.string.show_qr))

    val configFileLiveData = SingleLiveEvent<File>()
    val openConfigFile = SingleLiveEvent<File>()
    val storagePermissionsErrorLiveEvent = SingleLiveEvent<Any>()

    val serverConfigTitle = ObservableField(getString(R.string.server_config))
    val serverConfig = ObservableField<String>()

    var isInitialized: Boolean = false
    get() {
        return try {
            server.id
            true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    private var loadServersJob: Job? = null

    @OptIn(InternalCoroutinesApi::class)
    private fun loadServers(): Job {
        return launch {
            getDropletsUseCase.observe(
                onEmit = { value ->
                    run loop@{
                        value.forEach {
                            if (it.id == server.id) {
                                setServer1(it)
                                return@loop
                            }
                        }
                    }
                }
            )
        }
    }

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getDropletsUseCase.cancel()
        deleteServerUseCase.cancel()
        createServerUseCase.cancel()
    }

    private fun updateLanguage() {
        server.regionName?.let {
            region.set(getString(R.string.region, server.regionName!!))
        } ?: region.set("")

        address.set(getString(R.string.address, server.address))
        type.set(getString(R.string.type, server.typedConfig.getName()))

        serverConfigTitle.set(getString(R.string.server_config))
        qrTitleText.set(getString(R.string.show_qr))
    }

    override fun onConnect() {
        if (server.environmentStatus == SshManager.Status.READY) {
            if (VpnHelper.prepare(application) == null) {
                if (!isConnected) {
                    if (server.typedConfig is TypedConfig.Shadowsocks) {
                        if ((server.typedConfig as TypedConfig.Shadowsocks).isV2Ray()) {
                            if (!PluginManager.isV2RayEnabled()) {
                                v2RayRequireEvent.call()
                                return
                            }
                        }
                    }

                    startVpn(server.typedConfig)
                } else {
                    connectionType?.stop()
                }
            } else {
                vpnNotPreparedLiveEvent.call()
            }
        } else {
            when (server.environmentStatus) {
                SshManager.Status.ERROR -> {
                    deleteServer()
                }
                SshManager.Status.PENDING -> {
                    prepareServer()
                }
                else -> {}
            }
        }
    }

    private fun startVpn(typedConfig: TypedConfig) {
        connectionType = connectionTypeBuilder
            .setTypedConfig(typedConfig)
            .build()
    }

    fun onQrClick() {
        if (server.typedConfig is TypedConfig.HasQrCode) {

            val qrData = server.typedConfig as TypedConfig.HasQrCode
            if (qrData.getQrData() != null) {
                navigateToQrScreen(qrData.getQrData()!!)
            }
        }
    }

    private fun navigateToQrScreen(config: String) {
        router.navigateTo(Screens.QrScreen(config))
    }

    fun setConnectionStatus(level: ConnectionLevel) {
        updateConnectButtonWithVpnStatus(level)

        when (level) {
            ConnectionLevel.CONNECTED -> {
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
                        mainThread {
                            showProgress(getLogByStatus(getLocaleContext(), log))
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
                FileManager.createTempFile(server.name, ".ovpn", server.typedConfig.getConfig()!!)
            }

            is TypedConfig.WireGuard -> {
                FileManager.createTempFile(server.name, ".conf", server.typedConfig.getConfig()!!)
            }

            else -> {
                FileManager.createTempFile(server.name, ".txt", server.typedConfig.getConfig()!!)
            }
        }

    }

    fun setServer1(server: Server) {
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
            showAlertDialog(
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

        if ((server.typedConfig is TypedConfig.HasQrCode)
            && server.environmentStatus == SshManager.Status.READY
        ) {
            isQrVisible.set(true)
        } else {
            isQrVisible.set(false)
        }

        serverConfig.set(server.getConfig())

        updateLanguage()

        if (ServiceConnectionType.isActive()) {
            val connectionType = ServiceConnectionType.getCurrentConnectionType()

            if (this.server.id == connectionType!!.server!!.id) {
                this.connectionType = connectionType
            }
        }
    }

    private fun getStatus(): String {

        return when (server.environmentStatus) {
            SshManager.Status.STARTING -> getString(R.string.starting)
            SshManager.Status.READY -> getString(R.string.ready)
            SshManager.Status.ERROR -> getString(R.string.error)
            SshManager.Status.IN_PROCESS -> getString(R.string.in_process)
            SshManager.Status.PENDING -> getString(R.string.pending)
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
        return getProviderIcon(server.providerId)
    }

    private fun updateConnectButtonWithVpnStatus(level: ConnectionLevel) {
        connectButtonColor.set(getConnectButtonColor(level))
        connectButtonTitle.set(getConnectButtonTitle(level))
    }

    private fun updateConnectButtonWithServerStatus(status: SshManager.Status) {
        isConnectButtonEnable.set(getConnectButtonAvailability(status))
        connectButtonColor.set(getConnectButtonColor(status))
        connectButtonTitle.set(getConnectButtonTitle(status))
    }

    @DrawableRes
    private fun getConnectButtonColor(level: ConnectionLevel): Int {
        return when(level) {
            ConnectionLevel.CONNECTED -> {
                R.attr.colorError
            }

            ConnectionLevel.IDLE -> {
                R.attr.colorPrimary
            }

            else -> {
                R.attr.pendingColor
            }
        }
    }

    private fun getConnectButtonTitle(level: ConnectionLevel): String {
        return when(level) {
            ConnectionLevel.CONNECTED -> {
                getString(R.string.disconnect_action)
            }

            ConnectionLevel.IDLE -> {
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
                val type = Type.getType(typedConfig = server.typedConfig)

                if (type == null) {
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
        return if (Provider.getProviderById(server.providerId) is Provider.Custom) {
            getString(R.string.custom_server)
        } else {
            server.providerName
        }
    }

    override fun onBackPressed(): Boolean {
        return isNavigationEnable
    }
}