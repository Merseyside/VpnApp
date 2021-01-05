package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.EasyAccessRepositoryImpl
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.easyAccess.EasyAccessApi
import com.merseyside.dropletapp.easyAccess.EasyAccessRouter
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.utils.ktor.addHeader
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private val easyAccessModule = DI.Module("easy_access") {

    bind<HttpClient>() with singleton {

        val subscriptionManager: SubscriptionManager = instance()
        val settingsHelper: SettingsHelper = instance()

        HttpClient(instance()) {

            defaultRequest {
                accept(ContentType.Application.Json)
                val subscriptionInfo = subscriptionManager.getSubscriptionInfo()

                subscriptionInfo?.let {
                    addHeader(SUBSCRIPTION_ID, it.subscriptionId)
                    addHeader(TOKEN, it.token)
                    addHeader(LANGUAGE, settingsHelper.getLocale())
                }

            }
        }
    }

    bind<EasyAccessRouter>() with singleton { EasyAccessRouter(instance()) }

    bind<EasyAccessApi>() with singleton { EasyAccessApi( instance()) }

    bind<EasyAccessRepository>() with singleton { EasyAccessRepositoryImpl(instance(), instance()) }
}

internal val easyAccessComponent = DI {

    extend(appComponent)

    import(easyAccessModule)
}

private const val SUBSCRIPTION_ID = "Subscription-id"
private const val TOKEN = "Token"
private const val LANGUAGE = "Accept-Language"