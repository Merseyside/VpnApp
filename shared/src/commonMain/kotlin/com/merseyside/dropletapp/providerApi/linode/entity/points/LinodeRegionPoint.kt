package com.merseyside.dropletapp.providerApi.linode.entity.points

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinodeRegionPoint(
    @SerialName("id")
    val slug: String,

    @SerialName("country")
    val name: String,

    @SerialName("capabilities")
    val capabilities: List<String>

)