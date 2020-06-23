package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.subscriptions.entity.response.SubscriptionResponse

interface SubscriptionRepository {

    suspend fun getSubscriptions(): List<SubscriptionResponse>
}