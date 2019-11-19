package com.merseyside.dropletapp.providerApi.cryptoServers

import com.merseyside.dropletapp.providerApi.*
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.point.CryptoDropletInfoPoint
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.point.CryptoServerPoint
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.response.CryptoCreateSshKeyResponse
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.response.CryptoIsTokenValidResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.DigitalOceanCreateDropletResponse
import com.merseyside.dropletapp.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.BadResponseStatusException
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.list
import kotlinx.serialization.parse

class CryptoServersResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json.nonstrict
    private val serializer = io.ktor.client.features.json.defaultSerializer()

    private val baseUrl = "https://cryptoservers.net/api/v1"

    private val client by lazy {
        HttpClient(httpClientEngine) {
            engine {
                response.apply {
                    //defaultCharset = Charsets.UTF_8
                }
            }

            defaultRequest {
                accept(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }

    private fun getRoute(method: String): String{
        return "$baseUrl/$method"
    }

    private fun getAuthHeader(token: String): String {
        return "$token"
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getAccountInfo(token: String): CryptoIsTokenValidResponse {
        val apiMethod = "account"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getRegions(token: String): List<RegionPoint> {
        val apiMethod = "regions"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(RegionPoint.serializer().list, call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun createKey(token: String, publicKey: String): CryptoCreateSshKeyResponse {
        val apiMethod = "ssh/create"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                KEY to JsonPrimitive(publicKey)
            ))

            body = serializer.write(obj)
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun createDroplet(
        token: String,
        regionSlut: String,
        sshKeyId: Long
    ): DigitalOceanCreateDropletResponse {
        val apiMethod = "droplet/create"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                REGION_KEY to JsonPrimitive(regionSlut),
                SSH_KEY_ID to JsonPrimitive(sshKeyId)
            ))

            body = serializer.write(obj)
        }

        Logger.logMsg(TAG, call)
        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getDropletInfo(token: String, dropletId: Long): CryptoDropletInfoPoint {
        val apiMethod = "droplet/check/$dropletId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    suspend fun deleteDroplet(token: String, dropletId: Long) {
        val apiMethod = "droplet/delete/$dropletId"

        try {
            val call = client.get<String> {
                url.takeFrom(getRoute(apiMethod))

                header(AUTHORIZATION_KEY, getAuthHeader(token))
            }

            Logger.logMsg(TAG, call)
        } catch (e: BadResponseStatusException) {}
    }

    suspend fun deleteSshKey(token: String, dropletId: Long, sshKeyId: Long) {
        val apiMethod = "ssh/delete/$dropletId/$sshKeyId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        Logger.logMsg(TAG, call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getServerList(token: String): List<CryptoServerPoint> {

        val apiMethod = "droplet/list"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        Logger.logMsg(TAG, call)
        return json.parse(CryptoServerPoint.serializer().list, call)
    }

    companion object {
        private const val TAG = "CryptoServersResponseCreator"
    }

}