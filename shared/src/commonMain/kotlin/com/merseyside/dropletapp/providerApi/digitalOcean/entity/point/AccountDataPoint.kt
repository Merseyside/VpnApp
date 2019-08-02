package com.merseyside.dropletapp.providerApi.digitalOcean.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDataPoint(
    @SerialName("status")
    val status: String? = null,

    @SerialName("email")
    val email: String? = null
)