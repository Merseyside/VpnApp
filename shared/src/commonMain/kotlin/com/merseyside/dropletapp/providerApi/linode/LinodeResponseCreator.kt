package com.merseyside.dropletapp.providerApi.linode

import com.merseyside.dropletapp.providerApi.*
import com.merseyside.dropletapp.providerApi.linode.entity.response.LinodeAccountResponse
import com.merseyside.dropletapp.providerApi.linode.entity.response.LinodeCreateDropletResponse
import com.merseyside.dropletapp.providerApi.linode.entity.response.LinodeImportKeyResponse
import com.merseyside.dropletapp.providerApi.linode.entity.response.LinodeRegionsResponse
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

class LinodeResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json.nonstrict
    private val serializer = io.ktor.client.features.json.defaultSerializer()

    private val baseUrl = "https://api.linode.com/v4"

    private fun getRoute(method: String): String{
        return "$baseUrl/$method"
    }

    private fun getAuthHeader(token: String): String {
        return "Bearer $token"
    }

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

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getAccountInfo(token: String): LinodeAccountResponse {

        val apiMethod = "account"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getRegions(): LinodeRegionsResponse {

        val apiMethod = "regions"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))
        }

        return json.parse(call)

    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun importSshKey(token: String, label: String, publicKey: String): LinodeImportKeyResponse {
        val apiMethod = "profile/sshkeys"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val json = JsonObject(mapOf(
                LABEL_KEY to JsonPrimitive(label),
                SSH_KEY to JsonPrimitive(publicKey)
            ))

            body = serializer.write(json)
        }

        return json.parse(call)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun createLinode(
        token: String,
        label: String,
        regionSlug: String,
        sshKey: String?,
        tag: String
    ): LinodeCreateDropletResponse {

        val apiMethod = "linode/instances"

        val call = client.post<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))

            val json = JsonObject(mapOf(
                LABEL_KEY to JsonPrimitive(label),
                REGION_KEY to JsonPrimitive(regionSlug),
                TYPE_KEY to JsonPrimitive("g6-nanode-1"),
                IMAGE_KEY to JsonPrimitive("linode/debian9"),
                AUTHORIZED_KEYS to JsonArray(listOf(JsonPrimitive(sshKey))),
                ROOT_PASS_KEY to JsonPrimitive("Upstream"),
                TAG_KEY to JsonArray(listOf(JsonPrimitive(tag)))
            ))

            body = serializer.write(json)
        }

        return json.parse(call)

    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun getLinode(
        token: String,
        linodeId: Long
    ): LinodeCreateDropletResponse {
        val apiMethod = "linode/instances/$linodeId"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }

        return json.parse(call)
    }

    suspend fun deleteLinode(token: String, linodeId: Long) {
        val apiMethod = "linode/instances/$linodeId"

        client.delete<String> {
            url.takeFrom(getRoute(apiMethod))

            header(AUTHORIZATION_KEY, getAuthHeader(token))
        }
    }
}