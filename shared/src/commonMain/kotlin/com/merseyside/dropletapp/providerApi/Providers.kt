package com.merseyside.dropletapp.providerApi

sealed class Provider {

    abstract fun getName(): String

    abstract fun getId(): Long



    class DigitalOcean : Provider() {
        override fun getName(): String {
            return "Digital Ocean"
        }

        override fun getId(): Long {
            return 0
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }
    }

    companion object {
        private var providers: List<Provider>? = null

        fun getAllServices(): List<Provider> {
            if (providers == null) {
                val services = ArrayList<Provider>()

                services.add(DigitalOcean())

                providers = services
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