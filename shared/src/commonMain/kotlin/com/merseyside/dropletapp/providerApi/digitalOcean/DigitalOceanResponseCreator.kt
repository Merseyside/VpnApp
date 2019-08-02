package com.merseyside.dropletapp.providerApi.digitalOcean

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.AUTHORIZATION_KEY
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.IsTokenValidResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.takeFrom
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse

class DigitalOceanResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json.nonstrict

    companion object {
        private const val TAG = "DigitalOceanResponseCreator"
    }

    private val baseUrl = "https://api.digitalocean.com/v2/"

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
}