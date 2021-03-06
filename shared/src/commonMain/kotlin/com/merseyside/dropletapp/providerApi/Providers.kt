package com.merseyside.dropletapp.providerApi

import kotlinx.serialization.Serializable

@Serializable
sealed class Provider {

    abstract fun getName(): String

    abstract fun getId(): Long

    @Serializable
    class DigitalOcean : Provider() {
        override fun getName(): String {
            return "Digital Ocean"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 1
        }
    }

    @Serializable
    class Linode : Provider() {
        override fun getName(): String {
            return "Linode"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 2
        }
    }

    @Serializable
    class CryptoServers : Provider() {
        override fun getName(): String {
            return "Crypto Servers"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 0
        }
    }

    @Serializable
    class Custom : Provider() {
        override fun getName(): String {
            return "Custom"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 3
        }

    }

    companion object {
        private var providers: List<Provider>? = null

        fun getAllServices(): List<Provider> {
            if (providers == null) {
                val providers = ArrayList<Provider>()

                providers.add(CryptoServers())
                providers.add(DigitalOcean())
                providers.add(Linode())
                providers.add(Custom())

                this.providers = providers
            }

            return providers!!
        }

        fun getProviderById(providerId: Long): Provider? {
            getAllServices().forEach {
                if (it.getId() == providerId) {
                    return it
                }
            }

            return null
        }
    }
}