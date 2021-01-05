package com.merseyside.dropletapp.subscriptions

import com.merseyside.dropletapp.BuildKonfig
import com.merseyside.dropletapp.subscriptions.entity.response.SubscriptionResponse
import com.merseyside.kmpMerseyLib.utils.ktor.KtorRouter
import com.merseyside.kmpMerseyLib.utils.ktor.get
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.builtins.ListSerializer

class SubscriptionsRouter(httpClientEngine: HttpClientEngine): KtorRouter(
    baseUrl = BuildKonfig.host,
    httpClientEngine = httpClientEngine
) {

    suspend fun getSubscriptions(): List<SubscriptionResponse> {
        return get(method = "tariffs", deserializationStrategy = ListSerializer(SubscriptionResponse.serializer()))
    }
}