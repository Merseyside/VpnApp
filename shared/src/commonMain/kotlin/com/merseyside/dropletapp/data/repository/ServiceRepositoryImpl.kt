package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.entity.service.Service
import com.merseyside.dropletapp.domain.repository.ServiceRepository

class ServiceRepositoryImpl : ServiceRepository {

    override suspend fun getServices(): List<Service> {
        return services
    }

    companion object {

        private var services: List<Service> = Service.getAllServices()

    }
}