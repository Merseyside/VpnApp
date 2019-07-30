package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.entity.service.Service

interface ServiceRepository {

    suspend fun getServices(): List<Service>
}