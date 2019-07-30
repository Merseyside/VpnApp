package com.merseyside.dropletapp.data.entity.service

sealed class Service {

    abstract fun getName(): String

    abstract fun getId(): Long



    class DigitalOceanService : Service() {
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
        private var services: List<Service>? = null

        fun getAllServices(): List<Service> {
            if (services == null) {
                val services = ArrayList<Service>()

                services.add(DigitalOceanService())

                this.services = services
            }

            return this.services!!
        }

        fun getServiceById(serviceId: Long): Service? {
            getAllServices().forEach {
                if (it.getId() == serviceId) {
                    return it
                }
            }

            return null
        }
    }
}