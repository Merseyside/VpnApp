package com.merseyside.dropletapp.data.entity

import kotlinx.serialization.Serializable

@Serializable
sealed class TypedConfig {
    abstract var config: String?

    abstract fun getName(): String

    companion object {
        fun getNames(): List<String> {
            return arrayListOf(OpenVpn.name, WireGuard.name, L2TP.name, PPTP.name)
        }
    }

    @Serializable
    class OpenVpn(val userName: String): TypedConfig() {

        override var config: String? = null

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "OpenVpn"
        }
    }


    @Serializable
    class WireGuard: TypedConfig() {

        override var config: String? = null
        get() {
            return if (field != null) {
                val interfaceStr = "[Interface]\n"
                var split = field!!.split(interfaceStr)

                interfaceStr + split[1]
            } else field
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "WireGuard"
        }
    }

    @Serializable
    class L2TP(val userName: String, val password: String, val key: String): TypedConfig() {

        override var config: String? = null
            get() = "user=$userName\npassword=$password\nkey=$key"

        override fun equals(other: Any?): Boolean {
            return this === other
        }

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

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getName(): String {
            return name
        }

        companion object {
            const val name = "PPTP"
        }
    }
}