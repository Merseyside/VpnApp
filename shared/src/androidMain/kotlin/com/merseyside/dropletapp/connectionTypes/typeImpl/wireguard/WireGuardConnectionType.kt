package com.merseyside.dropletapp.connectionTypes.typeImpl.wireguard

import android.content.Context
import android.content.Intent
import com.merseyside.dropletapp.connectionTypes.AndroidImpl
import com.merseyside.dropletapp.connectionTypes.ConnectionLevel
import com.merseyside.dropletapp.connectionTypes.ServiceConnectionType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.kmpMerseyLib.utils.generateRandomString
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

actual class WireGuardConnectionType
    : ServiceConnectionType(), AndroidImpl, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    override var context: Context? = null
        set(value) {
            if (value != null) {
                field = value

                preparationIntent = prepare(value)
            }
        }

    private var tunnel: WireguardTunnel? = null
    private var backend: Backend? = null

    override fun isPrepared(): Boolean {
        return preparationIntent == null
    }

    override fun start(server: Server) {
        super.start(server)

        launch {
            prepareTunnel()
            getBackend(context!!)?.setState(tunnel, Tunnel.State.UP, tunnel!!.getConfig())
                ?: throw IllegalArgumentException("Backend is null")

            sessionTime = getCurrentTimeMillis()
            notifySessionStarts()

            withContext(Dispatchers.Main) {
                callback?.onConnectionEvent(ConnectionLevel.CONNECTED)
            }
        }
    }

    override fun stop() {
        super.stop()

        getBackend(context!!)?.setState(tunnel, Tunnel.State.DOWN, tunnel!!.getConfig())
            ?: throw IllegalArgumentException("Backend is null")
    }

    private fun generateTunnelName(): String {
        return generateRandomString()
    }

    private fun prepareTunnel() {
        tunnel = WireguardTunnel(generateTunnelName(), config)
    }

    fun getBackend(context: Context): Backend? {

        if (backend == null) {
            backend = GoBackend(context)
        }

        return backend
    }

    companion object {
        private var preparationIntent: Intent? = null

        fun prepare(context: Context): Intent? {
            return GoBackend.VpnService.prepare(context)
        }
    }
}