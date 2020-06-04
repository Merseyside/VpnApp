package com.merseyside.dropletapp.agent.net

import com.merseyside.dropletapp.agent.entity.AgentResponse
import com.merseyside.dropletapp.data.cipher.AesCipher
import com.merseyside.kmpMerseyLib.utils.serialization.deserialize
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.delay

class Agent(
    httpClientEngine: HttpClientEngine,
    private val cipher: AesCipher
) {

    private val responseCreator: AgentResponseCreator = AgentResponseCreator(httpClientEngine)

    suspend fun makeRequestAgent(
        ip: String,
        port: String,
        aesKey: String
    ): AgentResponse? {

        repeat(50) {
            try {
                val response = responseCreator.makeAgentRequest(ip, port)

                if (response != null) {

                    cipher.setKey(aesKey)
                    val decodedAes = cipher.decrypt(response)

                    val agentResponse: AgentResponse = decodedAes.deserialize()

                    if (agentResponse.status.code == "completed") {
                        return agentResponse
                    } else if (agentResponse.status.code == "error") {
                        return null
                    }
                }

            } catch (e: Exception) {}

            delay(10000)
        }

        return null
    }

    companion object {
        private const val TAG = "Agent"
    }
}