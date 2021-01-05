package com.merseyside.dropletapp.providerApi.cryptoServers

import com.merseyside.dropletapp.data.exception.IllegalResponseCode
import com.merseyside.dropletapp.providerApi.*
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.providerApi.base.entity.response.ErrorResponse
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.point.CryptoDropletInfoPoint
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.point.CryptoServerPoint
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.response.CryptoCreateSshKeyResponse
import com.merseyside.dropletapp.providerApi.cryptoServers.entity.response.CryptoIsTokenValidResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.DigitalOceanCreateDropletResponse
import com.merseyside.dropletapp.utils.jsonContent
import com.merseyside.kmpMerseyLib.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.ResponseException
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class CryptoServersResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val baseUrl = "https://cryptoservers.net/api/v1"

    private val client by lazy {
        HttpClient(httpClientEngine) {

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
        return token
    }

    suspend fun getAccountInfo(token: String): CryptoIsTokenValidResponse {
        val apiMethod = "account"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.decodeFromString(call)
    }

    suspend fun getRegions(token: String): List<RegionPoint> {
        val apiMethod = "regions"

        val call = client.get<HttpStatement> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return call.execute { response ->
            if (response.status.value > 300) {
                throw IllegalResponseCode(
                    response.status.value,
                    json.decodeFromString(ErrorResponse.serializer(), response.readText()).error)
            } else {
                json.decodeFromString(ListSerializer(RegionPoint.serializer()), response.readText())
            }
        }
    }

    suspend fun createKey(token: String, publicKey: String): CryptoCreateSshKeyResponse {
        val apiMethod = "ssh/create"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                KEY to JsonPrimitive(publicKey)
            ))

            body = obj.jsonContent()
        }

        return json.decodeFromString(call)
    }

    suspend fun createDroplet(
        token: String,
        regionSlut: String,
        sshKeyId: Long,
        script: String
    ): DigitalOceanCreateDropletResponse {
        val apiMethod = "droplet/create"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                REGION_KEY to JsonPrimitive(regionSlut),
                SSH_KEY_ID to JsonPrimitive(sshKeyId),
                USER_DATA to JsonPrimitive(script)
            ))

            body = obj.jsonContent()
        }

        Logger.log(this, call)
        return json.decodeFromString(call)
    }

    suspend fun getDroplet(token: String, dropletId: Long): CryptoDropletInfoPoint {
        val apiMethod = "droplet/check/$dropletId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.decodeFromString(call)
    }

    suspend fun deleteDroplet(token: String, dropletId: Long) {
        val apiMethod = "droplet/delete/$dropletId"

        try {
            val call = client.get<String> {
                url.takeFrom(getRoute(apiMethod))

                header(AUTHORIZATION_KEY, getAuthHeader(token))
            }
        } catch (e: ResponseException) {}
    }

    suspend fun deleteSshKey(token: String, dropletId: Long, sshKeyId: Long) {
        val apiMethod = "ssh/delete/$dropletId/$sshKeyId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }
    }

    suspend fun getServerList(token: String): List<CryptoServerPoint> {

        val apiMethod = "droplet/list"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.decodeFromString(ListSerializer(CryptoServerPoint.serializer()), call)
    }
}