package com.merseyside.dropletapp.di

import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import io.ktor.util.InternalAPI
import okhttp3.logging.HttpLoggingInterceptor

@UseExperimental(InternalAPI::class)
internal actual fun getPlatformEngine(): HttpClientEngine {
    return OkHttpEngine(OkHttpConfig().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    })
}

actual var sqlDriver: SqlDriver? = null