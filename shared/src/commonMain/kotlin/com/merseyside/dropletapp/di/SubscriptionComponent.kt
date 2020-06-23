package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.SubscriptionRepositoryImpl
import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.subscriptions.SubscriptionsApi
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

internal val subscriptionModule = Kodein.Module("subs") {
    bind<SubscriptionsApi>() with singleton { SubscriptionsApi(instance()) }

    bind<SubscriptionRepository>() with singleton { SubscriptionRepositoryImpl(instance(), instance()) }
}

internal val subscriptionComponent = Kodein {
    extend(appComponent)

    import(subscriptionModule)
}