package com.merseyside.dropletapp.freeAccess

import com.merseyside.dropletapp.freeAccess.entity.response.FreeAccessResponse
import com.merseyside.kmpMerseyLib.utils.ktor.KtorRouter
import com.merseyside.kmpMerseyLib.utils.ktor.get
import com.merseyside.kmpMerseyLib.utils.ktor.post
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.takeFrom
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse


class FreeAccessRouter(httpClientEngine: HttpClientEngine)
    : KtorRouter(
    baseUrl = "https://myvpn.run/api/v1",
    httpClientEngine = httpClientEngine
) {

    @OptIn(ImplicitReflectionSerializer::class)
    suspend fun getConfig(type: String): FreeAccessResponse {
        return get("tunnel/join", "type" to type)
    }
}