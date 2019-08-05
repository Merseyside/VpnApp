package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanCreateDropletResponse(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("created_at")
    val createDate: String
)