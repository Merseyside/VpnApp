package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.db.createDatabase
import com.merseyside.dropletapp.vpnApi.net.DigitalOceanApi
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

internal expect fun getPlatformEngine(): HttpClientEngine

expect var sqlDriver: SqlDriver?

internal val databaseModule = Kodein.Module("database") {

    bind<VpnDatabase>() with singleton {
        createDatabase(sqlDriver!!)
    }
}

internal val networkModule = Kodein.Module("network") {

    bind<HttpClientEngine>() with singleton { getPlatformEngine() }

    bind<DigitalOceanApi>() with singleton { DigitalOceanApi() }
}

internal val appComponent = Kodein {
    import(databaseModule)
    import(networkModule)
}