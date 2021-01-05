package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.SubscriptionRepositoryImpl
import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.subscriptions.SubscriptionsApi
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val subscriptionModule = DI.Module("subs") {
    bind<SubscriptionsApi>() with singleton { SubscriptionsApi(instance()) }

    bind<SubscriptionRepository>() with singleton { SubscriptionRepositoryImpl(instance(), instance()) }
}


internal val subscriptionComponent = DI {
    extend(appComponent)

    import(subscriptionModule)
}