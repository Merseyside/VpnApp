package com.merseyside.dropletapp.providerApi.digitalOcean

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateSshKeyResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
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

    override suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long,
        tag: String
    ): CreateDropletResponse {

        val response = responseCreator.createDroplet(token, name, regionSlug, sshKeyId, tag)

        return response.dropletPoint.let {
            CreateDropletResponse(
                id = it.id,
                name = it.name,
                createTime = it.createDate)
        }
    }

    override suspend fun getRegions(token: Token): List<RegionPoint> {
        val response = responseCreator.getRegions(token)

        return response.regionList.filter { it.isAvailable }
    }

    override suspend fun createKey(token: String, name: String, publicKey: String): CreateSshKeyResponse {
        return responseCreator.createKey(token, name, publicKey).sshKeyPoint.let {
            CreateSshKeyResponse(
                id = it.id,
                fingerprint = it.fingerprint
            )
        }
    }

    override suspend fun getDropletInfo(token: String, dropletId: Long): DropletInfoResponse {
        val response = responseCreator.getDropletInfo(token, dropletId)

        return response.dropletInfoPoint.let {
            DropletInfoResponse(
                id = it.id,
                name = it.name,
                status = it.status,
                createdAt = it.createdTime,
                networks = it.networkList.values.flatMap { networkPoints ->
                    val networkList = ArrayList<NetworkPoint>()

                    networkPoints.forEach { networkPoint ->
                        networkList.add(NetworkPoint(
                            ipAddress = networkPoint.ipAsddress,
                            netmask = networkPoint.netmask,
                            gateway = networkPoint.gateway
                        ))
                    }

                    networkList
                }
            )
        }
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