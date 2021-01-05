package com.merseyside.dropletapp.domain.model

import android.content.Context
import com.merseyside.dropletapp.di.appContext
import com.merseyside.filemanager.utils.getAssetContent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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

        actual fun build(): OAuthConfig? {
            val strConfig = getJsonFromAssets(jsonFilename, appContext!!)

            if (!strConfig.isNullOrEmpty()) {
                val json = Json {
                    isLenient = true
                }

                return json.decodeFromString(strConfig)
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