package com.merseyside.dropletapp.providerApi.cryptoServers

import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse
import com.merseyside.dropletapp.utils.Logger
import com.merseyside.dropletapp.utils.isDropletValid
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.delay
import kotlin.jvm.Synchronized

class CryptoServersProvider private constructor(httpClientEngine: HttpClientEngine) : ProviderApi {

    private val responseCreator = CryptoServersResponseCreator(httpClientEngine)

    override suspend fun isTokenValid(token: String): Boolean {
        return true
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
        Logger.logMsg("Script", script)

        val response = responseCreator.createDroplet(token, regionSlug, sshKeyId!!, script)

        if (response.dropletPoint.id <= 0L) throw IllegalStateException("Error while creating droplet")
        else responseCreator.deleteSshKey(token, response.dropletPoint.id, sshKeyId)

        repeat(REPEAT_COUNT) {
            val infoResponse = getDropletInfo(token, response.dropletPoint.id)

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

    private suspend fun getDropletInfo(token: String, dropletId: Long): DropletInfoResponse {
        val response = responseCreator.getDroplet(token, dropletId)

        return response.let {
            DropletInfoResponse(
                id = dropletId,
                name = it.name,
                status = it.status,
                createdAt = "",
                regionName = it.regionPoint.name,
                networks = it.networkList.map { cryptoNetworkPoint ->
                    NetworkPoint(
                        cryptoNetworkPoint.ipAddress,
                        cryptoNetworkPoint.netmask,
                        cryptoNetworkPoint.gateway
                    )
                }
            )
        }
    }

    override suspend fun getRegions(token: String): List<RegionPoint> {
        val response = responseCreator.getRegions(token)

        return response.filter { it.isAvailable }
    }

    override suspend fun importKey(
        token: String,
        name: String,
        publicKey: String
    ): ImportSshKeyResponse {
        return ImportSshKeyResponse(
            id = responseCreator.createKey(token, publicKey).id,
            fingerprint = null
        )
    }

    override suspend fun deleteDroplet(token: String, dropletId: Long) {
        responseCreator.deleteDroplet(token, dropletId)
    }

    override suspend fun addFloatingAddress(token: String, dropletId: Long): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun isServerAlive(token: String, serverId: Long): Boolean {
        return getDropletInfo(token, serverId).status == "active"
    }

    companion object {

        private var instance: CryptoServersProvider? = null

        @Synchronized
        fun getInstance(httpClientEngine: HttpClientEngine): CryptoServersProvider {
            if (instance == null) {
                instance = CryptoServersProvider(httpClientEngine)
            }

            return instance!!
        }

        private const val TAG = "CryptoServersProvider"

        private const val REPEAT_COUNT = 10
        private const val DELAY_MILLIS = 7000L
    }


}