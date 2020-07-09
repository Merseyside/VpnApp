package com.merseyside.dropletapp.connectionTypes

import com.merseyside.dropletapp.data.entity.TypedConfig


enum class Type {

    OPENVPN, WIREGUARD, SHADOWSOCKS;

    fun getTypeForApi(): String {
        return when {
            this == OPENVPN -> {
                "ovpn"
            }
            this == SHADOWSOCKS -> {
                "shadowsocks"
            }
            else -> {
                "wireguard"
            }
        }
    }

    companion object {
        fun getType(typedConfig: TypedConfig): Type? {
            return when (typedConfig) {
                is TypedConfig.OpenVpn -> OPENVPN
                is TypedConfig.WireGuard -> WIREGUARD
                is TypedConfig.Shadowsocks -> SHADOWSOCKS
                else -> null
            }
        }

        fun getTypeConfig(type: Type, config: String? = null): TypedConfig {
            return when (type) {
                OPENVPN -> {
                    TypedConfig.OpenVpn("user")
                }
                WIREGUARD -> {
                    TypedConfig.WireGuard()
                }
                else -> {
                    TypedConfig.Shadowsocks("")
                }
            }.apply {
                this.config = config
            }
        }
    }
}