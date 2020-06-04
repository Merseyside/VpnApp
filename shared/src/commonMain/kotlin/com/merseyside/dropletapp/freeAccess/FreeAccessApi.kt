package com.merseyside.dropletapp.freeAccess

import com.merseyside.dropletapp.data.exception.IllegalResponseCode
import io.ktor.client.engine.HttpClientEngine

class FreeAccessApi(httpClientEngine: HttpClientEngine) {

    private val responseCreator: FreeAccessResponseCreator = FreeAccessResponseCreator(httpClientEngine)

    suspend fun getConfig(): String {
        val response = responseCreator.getConfig()

        if (response.code == 200) {
            return response.data.access.config
        } else {
            throw IllegalResponseCode(response.code, response.message)
        }
    }
}