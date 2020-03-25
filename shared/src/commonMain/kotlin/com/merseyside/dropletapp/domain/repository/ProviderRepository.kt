package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.ssh.ConnectionType
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {

    suspend fun getProviders(): List<Provider>

    suspend fun getTypedConfigNames(): List<String>

    suspend fun getProvidersWithToken(): List<Provider>

    suspend fun getRegions(providerId: Long): List<RegionPoint>

    suspend fun createServer(
        providerId: Long,
        regionSlug: String,
        typeName: String,
        isV2RayEnabled: Boolean? = null,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun createCustomServer(
        typeName: String,
        userName: String,
        host: String,
        port: Int,
        password: String?,
        sshKey: String?,
        isV2RayEnabled: Boolean?,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun createServer(
        dropletId: Long,
        providerId: Long,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    fun getDropletsFlow(): Flow<List<Server>>

    suspend fun deleteDroplet(providerId: Long, dropletId: Long): Boolean

    suspend fun getTypedConfig(server: ServerModel): TypedConfig


}