package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse

interface ProviderApi {

    suspend fun isTokenValid(token: String): Boolean

    suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long? = null,
        sshKey: String? = null,
        tag: String
    ): DropletInfoResponse

    suspend fun getRegions(token: String): List<RegionPoint>

    suspend fun importKey(token: String, name: String, publicKey: String): ImportSshKeyResponse

    suspend fun deleteDroplet(token: String, dropletId: Long)

    suspend fun addFloatingAddress(token: String, dropletId: Long): String?
}