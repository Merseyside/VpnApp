package com.merseyside.dropletapp.subscriptions

import com.merseyside.dropletapp.domain.model.SubscriptionInfo

expect class SubscriptionManager private constructor() {

    fun unsubscribe()

    suspend fun isSubscribed(): Boolean

    fun getSubscriptionInfo(): SubscriptionInfo?
}