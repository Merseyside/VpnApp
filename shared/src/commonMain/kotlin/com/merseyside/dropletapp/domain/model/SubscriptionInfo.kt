package com.merseyside.dropletapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionInfo(
    val subscriptionId: String,
    val token: String,
    val expiryTime: Long
)