package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

interface ProviderApi {

    suspend fun isTokenValid(token: String): Boolean

    suspend fun createDroplet()

    suspend fun getRegions(token: Token): List<RegionPoint>
}