package com.merseyside.dropletapp.data.entity

import com.merseyside.kmpMerseyLib.utils.serialization.deserialize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TypedConfig {
    internal abstract var config: String?

    open fun getConfig(): String? {
        return config
    }

    abstract fun getName(): String

    interface HasQrCode {
        fun getQrData(): String?
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || this::class != other::class) return false

        other as TypedConfig

        return this.config == other.config
    }

    override fun hashCode(): Int {
        return config?.hashCode() ?: 0
    }

    companion object {
        fun getNames(): List<String> {
            return arrayListOf(
                OpenVpn.name,
                WireGuard.name,
                L2TP.name,
                PPTP.name,
                Shadowsocks.name
            )
        }
    }

    @Serializable
    class OpenVpn(val userName: String): TypedConfig() {

        override var config: String? = null

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "OpenVpn"
        }
    }


    @Serializable
    class WireGuard: TypedConfig(), HasQrCode {

        override var config: String? = null

        override fun getQrData(): String? {
            return config
        }

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "WireGuard"
        }
    }

    @Serializable
    class L2TP(val userName: String, val password: String, val key: String)
        : TypedConfig() {

        override var config: String? = null
            get() = "user=$userName\npassword=$password\nkey=$key"

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "L2TP"
        }
    }

    @Serializable
    class PPTP(val userName: String, val password: String): TypedConfig() {

        override var config: String? = null
            get() = "user=$userName\npassword=$password"

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "PPTP"
        }
    }

    @Serializable
    class Shadowsocks(val password: String)
        : TypedConfig(), HasQrCode {

        override var config: String? = null

        override fun getConfig(): String? {
            return config?.deserialize(ShadowsocksResponse.serializer())?.getUserReadableConfig()
        }

        override fun getQrData(): String? {
            return config?.deserialize(ShadowsocksResponse.serializer())?.getQrData()
        }

        override fun getName(): String {
            return name
        }

        fun isV2Ray(): Boolean {
            return config!!.deserialize(ShadowsocksResponse.serializer()).plugin != null
        }

        @Serializable
        data class ShadowsocksResponse(
            @SerialName("server")
            val server: String,

            @SerialName("server_port")
            val port: String,

            @SerialName("local_port")
            val localPort: String,

            @SerialName("password")
            val password: String,

            @SerialName("method")
            val method: String,

            @SerialName("plugin")
            val plugin: String? = null,

            @SerialName("plugin_opts")
            val pluginOpts: String? = null
        ) {

            private fun getPlug(): String {
                return if (plugin != null) {
                    "?plugin=$plugin"
                } else {
                    ""
                }
            }

            fun getUserReadableConfig(): String {
                val stringBuilder = StringBuilder()

                stringBuilder.append("ip: $server\n")
                stringBuilder.append("password: $password\n")
                stringBuilder.append("Server port: $port\n")
                stringBuilder.append("Local port: $localPort\n")
                stringBuilder.append("Protocol Connection: ${getQrData()}")

                return stringBuilder.toString()
            }

            fun getQrData(): String {
                return "ss://$method:$password@$server:$port${getPlug()}"
            }
        }

        companion object {
            const val name = "Shadowsocks"
        }

    }
}