package com.merseyside.dropletapp.di

import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.*
import io.ktor.util.InternalAPI
import okhttp3.logging.HttpLoggingInterceptor

internal actual fun getPlatformEngine(): HttpClientEngine {
    return OkHttp.create {
        config {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }
}

actual var sqlDriver: SqlDriver? = null