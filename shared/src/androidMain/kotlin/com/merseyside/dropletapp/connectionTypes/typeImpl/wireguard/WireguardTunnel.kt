package com.merseyside.dropletapp.connectionTypes.typeImpl.wireguard

import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config

class WireguardTunnel(
    private var name: String,
    private val config: String
) : Tunnel {

    private var onChange: (state: Tunnel.State) -> Unit = {}
    private var state: Tunnel.State = Tunnel.State.DOWN

    override fun getName() = name

    override fun onStateChange(newState: Tunnel.State) {
        if (this.state != newState) {
            this.state = newState

            onChange.invoke(state)
        }
    }

    fun getConfig(): Config {
        return Config.parse(config.reader().buffered())
    }

    fun setOnStateChangeListener(onChange: (state: Tunnel.State) -> Unit) {
        this.onChange = onChange
    }


}