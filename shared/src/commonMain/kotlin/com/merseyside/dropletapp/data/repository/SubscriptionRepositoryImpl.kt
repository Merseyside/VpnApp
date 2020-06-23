package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.subscriptions.SubscriptionsApi
import com.merseyside.dropletapp.subscriptions.entity.response.SubscriptionResponse
import com.merseyside.dropletapp.utils.SettingsHelper

class SubscriptionRepositoryImpl(
    private val subscriptionsApi: SubscriptionsApi,
    private val settings: SettingsHelper
) : SubscriptionRepository {

    override suspend fun getSubscriptions(): List<SubscriptionResponse> {
        return subscriptionsApi.getSubscriptions()
    }

}