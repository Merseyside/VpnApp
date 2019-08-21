package com.merseyside.dropletapp.providerApi.amazon

import com.merseyside.dropletapp.providerApi.ProviderApi
import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.ImportSshKeyResponse
import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import io.ktor.client.engine.HttpClientEngine
import kotlin.jvm.Synchronized

class AmazonProvider private constructor(httpClientEngine: HttpClientEngine): ProviderApi {

    private val responseCreator = AmazonResponseCreator(httpClientEngine)

    override suspend fun isTokenValid(token: String): Boolean {
        val keys = token.split(" ")

        responseCreator.getBlueprints(keys.first(), keys.last())

        return false
    }

    override suspend fun createDroplet(
        token: String,
        name: String,
        regionSlug: String,
        sshKeyId: Long,
        tag: String
    ): CreateDropletResponse {
        throw NotImplementedError()
    }

    override suspend fun getDropletInfo(token: String, dropletId: Long): DropletInfoResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getRegions(token: String): List<RegionPoint> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun importKey(token: String, name: String, publicKey: String): ImportSshKeyResponse {
        val keys = token.split(" ")

        //val response = responseCreator.importKey(keys.first(), keys.last(), name, publicKey)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        if (response.errorCode.isNullOrEmpty()) {
//            //return ImportSshKeyResponse()
//        }
    }

    override suspend fun deleteDroplet(token: String, dropletId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addFloatingAddress(token: String, dropletId: Long): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        private var instance: AmazonProvider? = null

        @Synchronized
        fun getInstance(httpClientEngine: HttpClientEngine): AmazonProvider {
            if (instance == null) {
                instance = AmazonProvider(httpClientEngine)
            }

            return instance!!
        }
    }
}