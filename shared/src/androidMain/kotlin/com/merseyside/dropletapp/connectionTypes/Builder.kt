package com.merseyside.dropletapp.connectionTypes

import android.content.Context
import com.merseyside.dropletapp.connectionTypes.typeImpl.openVpn.OpenVpnConnectionType
import com.merseyside.dropletapp.connectionTypes.typeImpl.wireguard.WireGuardConnectionType
import com.merseyside.dropletapp.data.entity.TypedConfig

actual class Builder actual constructor() {


    private var context: Context? = null
    private var type: Type? = null
    private var conf: String? = null

    fun setContext(context: Context): Builder {
        this.context = context
        return this
    }

    actual fun setType(type: Type): Builder {
        this.type = type
        return this
    }

    actual fun setConfig(config: String): Builder {
        this.conf = config
        return this
    }

    actual fun setTypedConfig(typeConfig: TypedConfig): Builder {
        this.conf = typeConfig.getConfig()
        this.type = Type.getType(typeConfig)

        return this
    }

    actual fun build(): ServiceConnectionType {
        if (context == null) throw IllegalArgumentException("Context can not be null")
        if (type == null) throw IllegalArgumentException("Type can not be null")
        if (conf == null) throw IllegalArgumentException("Config can not be null")

        val connectionType = when (type) {
            Type.OPENVPN -> {
                OpenVpnConnectionType()
            }
            Type.WIREGUARD -> {
                WireGuardConnectionType()
            }
            Type.SHADOWSOCKS -> {
                OpenVpnConnectionType()
            }
            else -> {
                throw IllegalArgumentException("Type isn't supported yet")
            }
        }

        (connectionType as AndroidImpl).context = context
        (connectionType as ServiceConnectionType).config = conf!!

        return connectionType
    }

}