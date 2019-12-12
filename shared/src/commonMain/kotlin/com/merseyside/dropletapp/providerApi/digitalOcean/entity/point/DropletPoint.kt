package com.merseyside.dropletapp.providerApi.digitalOcean.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropletPoint(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("created_at")
    val createDate: String
)