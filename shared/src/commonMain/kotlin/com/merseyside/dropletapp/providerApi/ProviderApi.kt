package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateSshKeyResponse
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

    suspend fun getRegions(token: Token): List<RegionPoint>

    suspend fun createKey(token: String, name: String, publicKey: String): CreateSshKeyResponse
}