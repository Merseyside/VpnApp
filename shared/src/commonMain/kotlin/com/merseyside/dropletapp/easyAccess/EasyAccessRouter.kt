package com.merseyside.dropletapp.easyAccess

import com.merseyside.dropletapp.BuildKonfig
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.easyAccess.entity.response.TunnelResponse
import com.merseyside.kmpMerseyLib.utils.ext.log
import com.merseyside.kmpMerseyLib.utils.ktor.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.serialization.builtins.ListSerializer


class EasyAccessRouter(
    httpClient: HttpClient
): KtorRouter(
    baseUrl = BuildKonfig.host,
    client = httpClient
) {

    suspend fun getConfig(
        type: String,
        regionId: String
    ): TunnelResponse {
        return get(
            method = "tunnel/join",
            queryParams = *arrayOf("type" to type, "region" to regionId),
            onError = { t ->
                when (t) {
                    is SocketTimeoutException -> getConfig(type, regionId)
                    else -> throw t
                }
            }
        )
    }

    suspend fun getRegions(): List<RegionPoint> {
        return get(
            method = "regions",
            deserializationStrategy = ListSerializer(RegionPoint.serializer()),
            onError = { t ->
                when (t) {
                    is SocketTimeoutException -> getRegions()
                    else -> {
                        if (t::class.simpleName!!.contains("SSL")) getRegions()
                        else throw t
                    }
                }
            }
        )
    }
}