package com.merseyside.dropletapp.providerApi.base.entity.point

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPoint(
    val ipAddress: String,
    val netmask: String,
    val gateway: String
)