package com.merseyside.dropletapp.providerApi.linode

import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.DropletInfoPoint
import com.merseyside.dropletapp.providerApi.exception.InvalidTokenException
import io.ktor.client.engine.HttpClientEngine
import kotlin.jvm.Synchronized

class LinodeProvider private constructor(httpClientEngine: HttpClientEngine): ProviderApi {

    private val responseCreator = LinodeResponseCreator(httpClientEngine)

    override suspend fun isTokenValid(token: String): Boolean {

        val response = responseCreator.getAccountInfo(token)

        if (response.email != null) {
            return true
        }

        throw InvalidTokenException("Your token is not valid")
    }

    override suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long?,
        sshKey: String?,
        tag: String,
        script: String
    ): DropletInfoResponse {

        val response = responseCreator.createLinode(token, name, regionSlug, sshKey, tag)

        if (response.id >= 0L) {
            return response.let {
                DropletInfoResponse(
                    id = it.id,
                    name = it.name,
                    status = it.status,
                    createdAt = it.createdAt,
                    regionName = it.region,
                    networks = it.ip.map { ipAddress -> NetworkPoint(ipAddress = ipAddress) }
                )
            }
        } else throw IllegalArgumentException("Can't create valid droplet. Please try again")
    }

    override suspend fun getRegions(token: String): List<RegionPoint> {
        val response = responseCreator.getRegions()
        val regionMap = HashMap<String, Int>()

        return response.regionPoint.mapNotNull {
            if (it.capabilities.contains("Linodes")) {

                var country = getHumanReadeableRegion(it.name)

                country = if (regionMap.containsKey(country)) {
                    regionMap[country] = regionMap[country]!!.plus(1)

                    "$country ${regionMap[country]}"
                } else {
                    regionMap[country] = 1

                    "$country 1"
                }

                RegionPoint(
                    slug = it.slug,
                    name = country,
                    isAvailable = true
                )
            } else {
                null
            }
        }
    }

    override suspend fun importKey(token: String, name: String, publicKey: String): ImportSshKeyResponse {
        val response = responseCreator.importSshKey(token, name, publicKey)

        return ImportSshKeyResponse(response.id)
    }

    override suspend fun deleteDroplet(token: String, dropletId: Long) {
        responseCreator.deleteLinode(token, dropletId)
    }

    override suspend fun addFloatingAddress(token: String, dropletId: Long): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun isServerAlive(token: String, serverId: Long): Boolean {
        return responseCreator.getLinode(token, serverId).status == "running"
    }



    companion object {

        private var instance: LinodeProvider? = null

        @Synchronized
        fun getInstance(httpClientEngine: HttpClientEngine): LinodeProvider {
            if (instance == null) {
                instance = LinodeProvider(httpClientEngine)
            }

            return instance!!
        }

        private val regionMap = mapOf(
            "in" to "India",
            "ca" to "Canada",
            "us" to "United States",
            "au" to "Australia",
            "uk" to "United Kingdom",
            "sg" to "Singapore",
            "de" to "Germany",
            "jp" to "Japan"
        )

        private fun getHumanReadeableRegion(country: String): String {
            return if (regionMap.containsKey(country)) {
                regionMap[country] ?: error("")
            } else {
                country
            }
        }
    }
}