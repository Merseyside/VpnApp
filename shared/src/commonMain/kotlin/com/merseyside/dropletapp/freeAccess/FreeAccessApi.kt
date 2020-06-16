package com.merseyside.dropletapp.freeAccess

import com.merseyside.dropletapp.data.exception.IllegalResponseCode
import io.ktor.client.engine.HttpClientEngine

class FreeAccessApi(httpClientEngine: HttpClientEngine) {

    private val router: FreeAccessRouter = FreeAccessRouter(httpClientEngine)

    suspend fun getConfig(type: String): String {
        val response = router.getConfig(type)

        if (response.code == 200) {
            return response.data.access.config
        } else {
            throw IllegalResponseCode(response.code, response.message)
        }
    }
}