package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.FloatingAddressPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FloatingAddressResponse(
    @SerialName("floating_ip")
    val point: FloatingAddressPoint
)