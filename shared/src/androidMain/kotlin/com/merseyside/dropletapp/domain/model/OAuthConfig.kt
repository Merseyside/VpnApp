package com.merseyside.dropletapp.domain.model

import android.content.Context
import android.content.res.AssetManager
import com.merseyside.dropletapp.di.appContext
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
            val manager: AssetManager = context.assets

            return try {
                val file: InputStream = manager.open(filename)
                val formArray = ByteArray(file.available())
                file.read(formArray)
                file.close()
                String(formArray)
            } catch (e: IOException) {
                null
            }
        }
    }

    override fun toString(): String {
        return "OAuthConfig(authEndPoint='$authEndPoint', host='$host', clientId='$clientId', redirectUrl='$redirectUrl')"
    }
}