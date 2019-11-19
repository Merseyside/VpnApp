package com.merseyside.dropletapp.providerApi.cryptoServers.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoNetworkPoint(
    @SerialName("version")
    val version: Int,

    @SerialName("ipAddress")
    val ipAddress: String,

    @SerialName("netmask")
    val netmask: String?,

    @SerialName("gateway")
    val gateway: String
)