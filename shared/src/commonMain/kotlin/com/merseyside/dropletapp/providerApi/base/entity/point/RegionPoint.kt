package com.merseyside.dropletapp.providerApi.base.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionPoint(

    @SerialName("slug")
    val slug: String,

    @SerialName("name")
    val name: String,

    @SerialName("available")
    val isAvailable: Boolean
)