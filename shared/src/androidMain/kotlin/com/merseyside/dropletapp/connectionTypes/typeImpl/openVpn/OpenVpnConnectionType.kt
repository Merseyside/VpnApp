package com.merseyside.dropletapp.connectionTypes.typeImpl.openVpn

import android.content.*
import android.os.IBinder
import com.merseyside.dropletapp.connectionTypes.AndroidImpl
import com.merseyside.dropletapp.connectionTypes.ConnectionLevel
import com.merseyside.dropletapp.connectionTypes.ServiceConnectionType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.kmpMerseyLib.utils.Logger
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.*

actual class OpenVpnConnectionType : ServiceConnectionType(), AndroidImpl {

    override var context: Context? = null

    private var vpnService: OpenVPNService? = null
    private var isServiceBind = false

    override fun start(server: Server) {
        super.start(server)

        if (isServiceBind) {
            if (!VpnStatus.isVPNActive()) {
                vpnService!!.server = server
                VPNLaunchHelper.startOpenVpn(getVpnProfile(), context)

                sessionTime = getCurrentTimeMillis()
                notifySessionStarts()
            } else {
                callback?.onConnectionEvent(ConnectionLevel.CONNECTED)
            }
        } else {
            bind()
        }
    }

    override fun stop() {
        super.stop()

        ProfileManager.setConnectedVpnProfileDisconnected(context)
        if (vpnService != null) {
            if (vpnService!!.management != null)
                vpnService!!.management.stopVPN(false)
            vpnService!!.server = null
        }

        unbind()
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as OpenVPNService.LocalBinder
            vpnService = binder.service

            Logger.log(this, "on service connected")

            if (VpnStatus.isVPNActive() && vpnService!!.server != null) {

                val currentServer = vpnService!!.server as Server

                if (currentServer.id == server?.id) {
                    callback?.onConnectionEvent(ConnectionLevel.CONNECTED)
                    sessionTime = vpnService!!.sessionTime
                }
            } else if (server != null) {
                start(server!!)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            vpnService = null
        }
    }

    private fun registerReceivers() {
        context!!.registerReceiver(br, IntentFilter(BROADCAST_ACTION))
    }

    private fun unregisterReceivers() {
        context!!.unregisterReceiver(br)
    }

    private var br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            receiveStatus(intent)
        }
    }

    private fun receiveStatus(intent: Intent) {
        callback?.onConnectionEvent(getConnectionLevel(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status"))))
    }

    private fun bind() {
        registerReceivers()

        val intent = Intent(context, OpenVPNService::class.java)
        intent.action = OpenVPNService.START_SERVICE
        isServiceBind = context!!.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbind() {
        if (isServiceBind) {
            isServiceBind = false
            context!!.unbindService(mConnection)

            unregisterReceivers()
        }
    }

    private fun getConnectionLevel(status: VpnStatus.ConnectionStatus): ConnectionLevel {
        return when (status) {
            VpnStatus.ConnectionStatus.LEVEL_CONNECTED -> {
                ConnectionLevel.CONNECTED
            }
            VpnStatus.ConnectionStatus.LEVEL_NOTCONNECTED -> {
                ConnectionLevel.IDLE
            }
            else -> {
                ConnectionLevel.CONNECTING
            }
        }
    }

    private fun getVpnProfile(): VpnProfile {
        return UpstreamConfigParser.parseConfig(context, config)
    }

    companion object {

        private const val BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS"
    }
}