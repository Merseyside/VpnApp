package com.merseyside.dropletapp.agent.net

import com.merseyside.dropletapp.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.takeFrom
import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class AgentResponseCreator(private val httpClientEngine: HttpClientEngine) {

    @OptIn(UnstableDefault::class)
    private val json = Json {
        isLenient = false
        ignoreUnknownKeys = true
    }

    private val client by lazy {
        HttpClient(httpClientEngine) {
            defaultRequest {
                accept(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }



    @OptIn(ImplicitReflectionSerializer::class)
    suspend fun makeAgentRequest(
        ip: String,
        port: String
    ): String? {

        return try {
            val statement = client.get<HttpStatement> {
                url.takeFrom("http://$ip")
                //body = TextContent(script, ContentType.Application.FormUrlEncoded)
            }

            val call = statement.execute()

            Logger.logMsg(TAG, call.contentLength() ?: "0")

            call.content.readUTF8Line()
        } catch (e: SocketTimeoutException) {
            null
        }
    }

    companion object {
        private const val TAG = "AgentResponse"
    }
}