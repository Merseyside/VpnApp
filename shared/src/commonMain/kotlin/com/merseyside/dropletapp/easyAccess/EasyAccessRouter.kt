package com.merseyside.dropletapp.easyAccess

import com.merseyside.dropletapp.BuildKonfig
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.easyAccess.entity.response.TunnelResponse
import com.merseyside.kmpMerseyLib.utils.ktor.KtorRouter
import com.merseyside.kmpMerseyLib.utils.ktor.get
import io.ktor.client.*
import io.ktor.network.sockets.*
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
        return try {
            get(
                method = "tunnel/join",
                queryParams = arrayOf("type" to type, "region" to regionId),
            )
        } catch(e: SocketTimeoutException) {
            getConfig(type, regionId)
        } catch (e: Throwable) {
            throw e
        }
    }

    suspend fun getRegions(): List<RegionPoint> {
        return try {
            get(
                method = "regions",
                deserializationStrategy = ListSerializer(RegionPoint.serializer()),
            )
        } catch(e: SocketTimeoutException) {
            getRegions()
        } catch (e: Throwable) {
            if (e::class.simpleName!!.contains("SSL")) getRegions()
            else throw e
        }
    }
}