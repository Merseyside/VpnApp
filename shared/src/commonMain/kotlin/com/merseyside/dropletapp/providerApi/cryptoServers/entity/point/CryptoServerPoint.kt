package com.merseyside.dropletapp.providerApi.cryptoServers.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoServerPoint(
    @SerialName("id")
    val id: Long
)