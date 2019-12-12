package com.merseyside.dropletapp.providerApi.linode.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinodeCreateDropletResponse(

    @SerialName("label")
    val name: String,

    @SerialName("id")
    val id: Long,

    @SerialName("status")
    val status: String,

    @SerialName("ipv4")
    val ip: List<String>,

    @SerialName("created")
    val createdAt: String,

    @SerialName("region")
    val region: String
)