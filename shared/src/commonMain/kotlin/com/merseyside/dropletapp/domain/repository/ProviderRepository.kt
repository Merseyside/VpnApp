package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {

    suspend fun getProviders(): List<Provider>

    suspend fun getProvidersWithToken(): List<Provider>

    suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint>

    suspend fun createServer(
        token: Token,
        providerId: Long,
        regionSlug: String,
        serverName: String,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun createServer(
        dropletId: Long,
        providerId: Long,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    fun getDropletsFlow(): Flow<List<Server>>

    suspend fun deleteDroplet(token: Token, providerId: Long, dropletId: Long): Boolean

    suspend fun getOvpnFile(token: Token, dropletId: Long, providerId: Long): String
}