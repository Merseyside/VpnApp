package com.merseyside.dropletapp.easyAccess

import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.easyAccess.entity.response.TunnelResponse
import com.merseyside.kmpMerseyLib.utils.ext.log
import com.merseyside.kmpMerseyLib.utils.ktor.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.builtins.ListSerializer


class EasyAccessRouter(
    httpClient: HttpClient
): KtorRouter(
    baseUrl = "https://myvpn.run/api/v1",
    client = httpClient
) {

    suspend fun getConfig(
        type: String,
        regionId: String
    ): TunnelResponse {
        return get(
            method = "tunnel/join",
            queryParams = *arrayOf("type" to type, "region" to regionId)
        )
    }

    suspend fun getRegions(): List<RegionPoint> {
        return get(
            method = "regions",
            deserializationStrategy = ListSerializer(RegionPoint.serializer())
        )
    }
}