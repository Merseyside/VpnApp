package com.merseyside.dropletapp.providerApi.amazon

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json

class AmazonResponseCreator(private val httpClientEngine: HttpClientEngine) {

    private val json = Json.nonstrict
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

    suspend fun getBlueprints(accessKey: String, secretKey: String): List<String> {
        val requestPair = getRequest(
            method = "GET",
            action = "GetBlueprints",
            accessKey = accessKey,
            secretKey = secretKey)

        val call = client.get<String> {
            url.takeFrom(requestPair.first)

            requestPair.second.forEach {
                header(it.key, it.value)
            }
        }

        return listOf()
    }
}