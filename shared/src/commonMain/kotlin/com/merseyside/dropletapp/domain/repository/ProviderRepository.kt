package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

interface ProviderRepository {

    suspend fun getProviders(): List<Provider>

    suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint>

    suspend fun createServer(
        token: Token,
        providerId: Long,
        regionSlug: String,
        serverName: String
    ): Boolean

    suspend fun getServers(): List<Server>

    suspend fun deleteDroplet(token: Token, providerId: Long, dropletId: Long): Boolean
}