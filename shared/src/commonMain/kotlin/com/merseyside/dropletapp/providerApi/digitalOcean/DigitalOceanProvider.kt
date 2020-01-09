package com.merseyside.dropletapp.providerApi.digitalOcean

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.DropletInfoPoint
import com.merseyside.dropletapp.providerApi.exception.InvalidTokenException
import com.merseyside.dropletapp.utils.Logger
import com.merseyside.dropletapp.utils.isDropletValid
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.delay
import kotlin.jvm.Synchronized

class DigitalOceanProvider private constructor(httpClientEngine: HttpClientEngine) : ProviderApi {

    private val responseCreator = DigitalOceanResponseCreator(httpClientEngine)

    override suspend fun isTokenValid(token: String): Boolean {
        val response = responseCreator.getAccountInfo(token)

        if (response.accountDataPoint?.email != null && response.accountDataPoint.status == "active") {
            return true
        }

        throw InvalidTokenException(response.accountDataPoint?.message ?: "Unknown error")
    }

    override suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long?,
        sshKey: String?,
        tag: String
    ): DropletInfoResponse {

        val response = responseCreator.createDroplet(token, name, regionSlug, sshKeyId!!, tag)

        if (response.dropletPoint.id <= 0L) throw IllegalStateException("Error while creating droplet")

        repeat(REPEAT_COUNT) {
            val infoResponse = getDropletInfo(token, response.dropletPoint.id)

            //Logger.logMsg(TAG, )

            if (isDropletValid(infoResponse)) {
                return infoResponse
            } else {
                if (it == REPEAT_COUNT -1) {
                    deleteDroplet(token, response.dropletPoint.id)
                } else {
                    delay(DELAY_MILLIS)
                }
            }
        }

        throw IllegalArgumentException("Can't create valid droplet. Please try again")
    }

    override suspend fun getRegions(token: Token): List<RegionPoint> {
        val response = responseCreator.getRegions(token)

        return response.regionList.filter { it.isAvailable }
    }

    override suspend fun importKey(token: String, name: String, publicKey: String): ImportSshKeyResponse {
        return responseCreator.createKey(token, name, publicKey).sshKeyPoint.let {
            ImportSshKeyResponse(
                id = it.id,
                fingerprint = it.fingerprint
            )
        }
    }

    private suspend fun getDropletInfo(token: String, dropletId: Long): DropletInfoResponse {
        val response = responseCreator.getDropletInfo(token, dropletId)

        Logger.logMsg(TAG, response.toString())

        return response.dropletInfoPoint.let {
            DropletInfoResponse(
                id = it.id,
                name = it.name,
                status = it.status,
                createdAt = it.createdTime,
                regionName = it.regionPoint.name,
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

    override suspend fun deleteDroplet(token: String, dropletId: Long) {
        responseCreator.deleteDroplet(token, dropletId)
    }

    override suspend fun addFloatingAddress(token: String, dropletId: Long): String? {
        return responseCreator.addFloatingAddress(token, dropletId).point.address
    }

    override suspend fun isServerAlive(token: String, serverId: Long): Boolean {
        return getDropletInfo(token, serverId).status == "active"
    }

    companion object {

        const private val TAG = "DigitalOceanProvider"

        private var instance: DigitalOceanProvider? = null

        @Synchronized
        fun getInstance(httpClientEngine: HttpClientEngine): DigitalOceanProvider {
            if (instance == null) {
                instance = DigitalOceanProvider(httpClientEngine)
            }

            return instance!!
        }

        private const val REPEAT_COUNT = 5
        private const val DELAY_MILLIS = 7000L
    }


}