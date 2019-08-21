package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

interface ProviderApi {

    suspend fun isTokenValid(token: String): Boolean

    suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long,
        tag: String
    ): CreateDropletResponse

    suspend fun getDropletInfo(token: String, dropletId: Long): DropletInfoResponse

    suspend fun getRegions(token: String): List<RegionPoint>

    suspend fun importKey(token: String, name: String, publicKey: String): ImportSshKeyResponse

    suspend fun deleteDroplet(token: String, dropletId: Long)

    suspend fun addFloatingAddress(token: String, dropletId: Long): String?
}