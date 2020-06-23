package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.EasyAccessRepositoryImpl
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.easyAccess.EasyAccessApi
import com.merseyside.dropletapp.easyAccess.EasyAccessRouter
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.utils.ktor.addHeader
import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import kotlin.reflect.KProperty

private val easyAccessModule = Kodein.Module("easy_access") {

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

internal val easyAccessComponent = Kodein {

    extend(appComponent)

    import(easyAccessModule)
}

private const val SUBSCRIPTION_ID = "Subscription-id"
private const val TOKEN = "Token"
private const val LANGUAGE = "Accept-Language"