package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

interface ProviderRepository {

    suspend fun getServices(): List<Provider>

    suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint>

    suspend fun createServer(
        token: Token,
        providerId: Long,
        regionSlug: String,
        serverName: String
    ): Boolean
}