package com.merseyside.dropletapp.connectionTypes.typeImpl.wireguard

import android.content.Context
import com.merseyside.dropletapp.connectionTypes.AndroidImpl
import com.merseyside.dropletapp.connectionTypes.ConnectionLevel
import com.merseyside.dropletapp.connectionTypes.ServiceConnectionType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.kmpMerseyLib.domain.coroutines.applicationContext
import com.merseyside.kmpMerseyLib.domain.coroutines.computationContext
import com.merseyside.kmpMerseyLib.utils.Logger
import com.merseyside.kmpMerseyLib.utils.generateRandomString
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class WireGuardConnectionType
    : ServiceConnectionType(), AndroidImpl {

    override var context: Context? = null

    private var tunnel: WireguardTunnel? = null
    private var backend: Backend? = null

    override fun start(server: Server) {
        super.start(server)

        Logger.log(this, "config = $config")

        launch {
            withContext(computationContext) {
                prepareTunnel()
                getBackend(context!!)?.setState(tunnel, Tunnel.State.UP, tunnel!!.getConfig())
                    ?: throw IllegalArgumentException("Backend is null")

                sessionTime = getCurrentTimeMillis()
                notifySessionStarts()
            }

            callback?.onConnectionEvent(ConnectionLevel.CONNECTED)
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
}