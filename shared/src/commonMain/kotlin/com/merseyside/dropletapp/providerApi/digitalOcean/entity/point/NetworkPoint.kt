package com.merseyside.dropletapp.providerApi.digitalOcean.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPoint(

    @SerialName("ip_address")
    val ipAddress: String,

    @SerialName("netmask")
    val netmask: String,

    @SerialName("gateway")
    val gateway: String,

    @SerialName("type")
    val type: String
)