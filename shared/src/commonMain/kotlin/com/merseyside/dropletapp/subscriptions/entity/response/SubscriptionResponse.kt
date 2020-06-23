package com.merseyside.dropletapp.subscriptions.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionResponse(
    val title: String,
    val description: String,

    @SerialName("code")
    val subscriptionId: String
)