package com.merseyside.dropletapp.easyAccess

import com.merseyside.dropletapp.data.exception.IllegalResponseCode
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.easyAccess.exception.InvalidTokenException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

class EasyAccessApi(private val router: EasyAccessRouter) {

    suspend fun getConfig(
        type: String,
        regionId: String
    ): String {
        val response = router.getConfig(type, regionId)

        if (response.code == 200) {
            return response.data.access.config
        } else if (response.code == 400) {
            throw InvalidTokenException()
        } else {
            throw IllegalResponseCode(response.code, response.message)
        }
    }

    suspend fun getRegions(): List<RegionPoint> {
        return router.getRegions()
    }
}