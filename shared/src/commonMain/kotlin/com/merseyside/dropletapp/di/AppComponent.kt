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
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

internal expect fun getPlatformEngine(): HttpClientEngine

expect var sqlDriver: SqlDriver?
var subsManager: SubscriptionManager? = null

var connectionTypeBuilder: Builder? = null

internal val databaseModule = Kodein.Module("database") {

    bind<VpnDatabase>() with singleton {
        createDatabase(sqlDriver!!)
    }

    bind<Builder>() with singleton { connectionTypeBuilder!! }

    bind<SubscriptionManager>() with singleton { subsManager!! }
}

internal val networkModule = Kodein.Module("network") {

    bind<HttpClientEngine>() with singleton { getPlatformEngine() }

    bind<ProviderApiFactory>() with singleton { ProviderApiFactory( instance() ) }
}

internal val appModule = Kodein.Module("app") {
    bind<Settings>() with singleton { Settings() }

    bind<SettingsHelper>() with singleton { SettingsHelper(instance()) }
}

internal val appComponent = Kodein {
    import(appModule)
    import(databaseModule)
    import(networkModule)
}