package com.merseyside.dropletapp.domain.model

import android.content.Context
import android.content.res.AssetManager
import com.merseyside.dropletapp.di.appContext
import com.merseyside.filemanager.utils.getAssetContent
import kotlinx.io.InputStream
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import java.io.IOException

@Serializable
actual data class OAuthConfig actual constructor(
    actual var authEndPoint: String,
    actual var host: String,
    actual var clientId: String,
    actual var redirectUrl: String,
    actual var scopes: String
) {

    actual class Builder actual constructor(
        private val jsonFilename: String
    ) {

        @OptIn(ImplicitReflectionSerializer::class)
        actual fun build(): OAuthConfig? {
            val strConfig = getJsonFromAssets(jsonFilename, appContext!!)

            if (!strConfig.isNullOrEmpty()) {
                val json = Json {
                    isLenient = true
                }

                return json.parse(strConfig)
            }

            return null
        }

        @OptIn(InternalSerializationApi::class)
        private fun getJsonFromAssets(filename: String, context: Context): String? {
            return getAssetContent(context, filename)
        }
    }

    override fun toString(): String {
        return "OAuthConfig(authEndPoint='$authEndPoint', host='$host', clientId='$clientId', redirectUrl='$redirectUrl')"
    }
}