package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.db.createDatabase
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
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

    bind<ProviderApiFactory>() with singleton { ProviderApiFactory( instance() ) }
}

internal val appComponent = Kodein {
    import(databaseModule)
    import(networkModule)
}