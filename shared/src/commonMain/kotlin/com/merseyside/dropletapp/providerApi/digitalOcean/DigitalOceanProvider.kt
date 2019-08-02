package com.merseyside.dropletapp.providerApi.digitalOcean

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionResponse
import io.ktor.client.engine.HttpClientEngine
import kotlin.jvm.Synchronized

class DigitalOceanProvider private constructor(httpClientEngine: HttpClientEngine) : ProviderApi {

    private val responseCreator = DigitalOceanResponseCreator(httpClientEngine)

    override suspend fun isTokenValid(token: String): Boolean {
        val response = responseCreator.isTokenValid(token)

        return response.accountDataPoint?.let {
            it.email != null && it.status == "active"
        } ?: false
    }

    override suspend fun createDroplet() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRegions(token: Token): List<RegionPoint> {
        val response = responseCreator.getRegions(token)

        return response.regionList.filter { it.isAvailable }
    }

    companion object {

        private var instance: DigitalOceanProvider? = null

        @Synchronized
        fun getInstance(httpClientEngine: HttpClientEngine): DigitalOceanProvider {
            if (instance == null) {
                instance = DigitalOceanProvider(httpClientEngine)
            }

            return instance!!
        }
    }


}