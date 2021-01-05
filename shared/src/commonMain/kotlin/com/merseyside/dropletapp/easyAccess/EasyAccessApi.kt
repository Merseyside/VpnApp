package com.merseyside.dropletapp.easyAccess

import com.merseyside.dropletapp.data.exception.IllegalResponseCode
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.easyAccess.entity.response.TunnelResponse
import com.merseyside.dropletapp.easyAccess.exception.InvalidTokenException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

class EasyAccessApi(private val router: EasyAccessRouter) {

    suspend fun getTunnel(
        type: String,
        regionId: String
    ): TunnelResponse {
        val response = router.getConfig(type, regionId)

        when (response.code) {
            200 -> {
                return response
            }
            400 -> {
                throw InvalidTokenException()
            }
            else -> {
                throw IllegalResponseCode(response.code, response.message)
            }
        }
    }

    suspend fun getRegions(): List<RegionPoint> {
        return router.getRegions()
    }
}