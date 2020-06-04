package com.merseyside.dropletapp.freeAccess

import com.merseyside.dropletapp.freeAccess.entity.response.FreeAccessResponse
import com.merseyside.dropletapp.providerApi.AUTHORIZATION_KEY
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse

class FreeAccessResponseCreator(private val httpClientEngine: HttpClientEngine) {

    @OptIn(UnstableDefault::class)
    private val json = Json {
        isLenient = false
        ignoreUnknownKeys = true
    }

    private val baseUrl = "https://myvpn.run/api/v1"

    private val client by lazy {
        HttpClient(httpClientEngine) {
            defaultRequest {
                accept(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }

    private fun getRoute(method: String): String {
        return "$baseUrl/$method"
    }

    @OptIn(ImplicitReflectionSerializer::class)
    suspend fun getConfig(): FreeAccessResponse {
        val apiMethod = "tunnel/join"

        val call = client.get<String> {
            url.takeFrom(getRoute(apiMethod))
        }

        return json.parse(call)
    }
}