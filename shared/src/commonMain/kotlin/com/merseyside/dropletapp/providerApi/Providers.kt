package com.merseyside.dropletapp.providerApi

sealed class Provider {

    abstract fun getName(): String

    abstract fun getId(): Long

    class DigitalOcean : Provider() {
        override fun getName(): String {
            return "Digital Ocean"
        }



        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 0
        }
    }

    class Amazon : Provider() {
        override fun getName(): String {
            return "Amazon"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun getId(): Long {
            return 1
        }
    }

    companion object {
        private var providers: List<Provider>? = null

        fun getAllServices(): List<Provider> {
            if (providers == null) {
                val providers = ArrayList<Provider>()

                providers.add(DigitalOcean())
                providers.add(Amazon())

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