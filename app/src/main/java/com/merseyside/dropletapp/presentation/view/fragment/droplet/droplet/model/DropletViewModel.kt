package com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.domain.interactor.GetOvpnFileInteractor
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
    private val getOvpnFileUseCase: GetOvpnFileInteractor,
    private val createServerUseCase: CreateServerInteractor
) : BaseDropletViewModel(router), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    val providerIcon = ObservableField<Int>()
    val providerTitle = ObservableField<String>()

    val statusIcon = ObservableField<Int>()
    val statusColor = ObservableField<Int>()
    val status = ObservableField<String>()

    val connectionLiveData = SingleLiveEvent<Boolean>()
    val ovpnFileLiveData = MutableLiveData<File>()
    val vpnProfileLiveData = MutableLiveData<VpnProfile>()

    lateinit var server: Server
    private set

    private var isConnected = false
    set(value) {
        if (field != value) {
            field = value

            connectionLiveData.value = value
        }
    }

    init {
        loadServers()
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun loadServers() {
        launch {
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
        getOvpnFileUseCase.cancel()
    }

    override fun updateLanguage(context: Context) {}

    fun onConnectClick(server: Server) {

        if (!isConnected) {
            getOvpnFileUseCase.execute(
                params = GetOvpnFileInteractor.Params(server.id, server.providerId),
                onComplete = {
                    prepareVpn(it)
                },
                onError = { throwable ->
                    showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
                },
                showProgress = {
                    if (server.environmentStatus == SshManager.Status.IN_PROCESS) {
                        showProgress(getString(R.string.receiving_access_msg))
                    }
                },
                hideProgress = { hideProgress() }
            )
        } else {
            isConnected = false
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

    fun setServer(server: Server) {
        this.server = server

        providerIcon.set(getIcon())
        providerTitle.set(getTitle())

        statusIcon.set(getStatusIcon())
        statusColor.set(getStatusColor())
        status.set(getStatus())
    }

    fun setConnectionStatus(status: VpnStatus.ConnectionStatus) {
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

    fun setConnectionStatus(isConnected: Boolean) {
        this.isConnected = isConnected
    }

    fun prepareServer(server: Server) {
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
                loadServers()
            },
            onError = { throwable ->
                showErrorMsg(errorMsgCreator.createErrorMsg(throwable))
            },
            hideProgress = { hideProgress() }
        )
    }






    fun getStatus(): String {
        if (isConnected) {
            return VpnApplication.getInstance().getActualString(R.string.connected)
        }

        return when (server.environmentStatus) {
            SshManager.Status.READY -> VpnApplication.getInstance().getActualString(R.string.ready)
            SshManager.Status.ERROR -> VpnApplication.getInstance().getActualString(R.string.error)
            SshManager.Status.IN_PROCESS -> VpnApplication.getInstance().getActualString(R.string.in_process)
            SshManager.Status.PENDING -> VpnApplication.getInstance().getActualString(R.string.pending)
        }
    }

    fun getStatusColor(): Int {
        if (isConnected) {
            return R.attr.colorSecondary
        }

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
        if (isConnected) {
            return R.drawable.ic_connected
        }

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

    fun getTitle(): String {
        return server.providerName
    }
}