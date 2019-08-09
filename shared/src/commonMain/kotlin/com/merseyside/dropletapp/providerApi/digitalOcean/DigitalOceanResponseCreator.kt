package com.merseyside.dropletapp.providerApi.digitalOcean

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.*
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.parse

class DigitalOceanResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json.nonstrict

    private val baseUrl = "https://api.digitalocean.com/v2"

    private val serializer = io.ktor.client.features.json.defaultSerializer()

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

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun isTokenValid(token: String): IsTokenValidResponse {
        val apiMethod = "account"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getRegions(token: Token): RegionResponse {
        val apiMethod = "regions"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    private fun getAuthHeader(token: Token): String {
        return "Bearer $token"
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun createKey(token: String, name: String, publicKey: String): CreateSshKeyDigitalOceanResponse {
        val apiMethod = "account/keys"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                NAME_KEY to JsonPrimitive(name),
                PUBLIC_KEY to JsonPrimitive(publicKey)
            ))

            body = serializer.write(obj)
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun createDroplet(
        token: String,
        name: String,
        regionSlut: String,
        sshKeyId: Long,
        tag: String
    ): DigitalOceanCreateDropletResponse {
        val apiMethod = "droplets"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val obj = JsonObject(mapOf(
                NAME_KEY to JsonPrimitive(name),
                REGION_KEY to JsonPrimitive(regionSlut),
                SSH_KEY to JsonArray(listOf(
                    JsonPrimitive(sshKeyId)
                )),
                IMAGE_KEY to JsonPrimitive("debian-9-x64"),
                SIZE_KEY to JsonPrimitive("1gb"),
                BACKUPS_KEY to JsonPrimitive(false),
                TAG_KEY to JsonArray(listOf(
                    JsonPrimitive(tag)
                ))
            ))

            body = serializer.write(obj)
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getDropletInfo(token: String, dropletId: Long): DigitalOceanDropletInfoResponse {
        val apiMethod = "droplets/$dropletId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    suspend fun deleteDroplet(token: String, dropletId: Long) {
        val apiMethod = "droplets/$dropletId"

        client.delete<Any> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }
    }

    companion object {
        private const val TAG = "DigitalOceanResponseCreator"
    }
}