package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.connectionTypes.Builder
import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.db.createDatabase
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.SettingsHelper
import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal expect fun getPlatformEngine(): HttpClientEngine

expect var sqlDriver: SqlDriver?
var subsManager: SubscriptionManager? = null

var connectionTypeBuilder: Builder? = null

internal val databaseModule = DI.Module("database") {

    bind<VpnDatabase>() with singleton {
        createDatabase(sqlDriver!!)
    }

    bind<Builder>() with singleton { connectionTypeBuilder!! }

    bind<SubscriptionManager>() with singleton { subsManager!! }
}

internal val networkModule = DI.Module("network") {

    bind<HttpClientEngine>() with singleton { getPlatformEngine() }

    bind<ProviderApiFactory>() with singleton { ProviderApiFactory( instance() ) }
}

internal val appModule = DI.Module("app") {
    bind<Settings>() with singleton { Settings() }

    bind<SettingsHelper>() with singleton { SettingsHelper(instance()) }
}

internal val appComponent = DI {
    import(appModule)
    import(databaseModule)
    import(networkModule)
}