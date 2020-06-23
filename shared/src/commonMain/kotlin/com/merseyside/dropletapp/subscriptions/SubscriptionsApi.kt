package com.merseyside.dropletapp.subscriptions

import com.merseyside.dropletapp.subscriptions.entity.response.SubscriptionResponse
import io.ktor.client.engine.HttpClientEngine

class SubscriptionsApi(httpClientEngine: HttpClientEngine) {

    val router = SubscriptionsRouter(httpClientEngine)

    suspend fun getSubscriptions(): List<SubscriptionResponse> {
        return router.getSubscriptions()
    }
}