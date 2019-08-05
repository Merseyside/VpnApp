package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.NetworkPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanDropletInfoResponse(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdTime: String,

    @SerialName("networks")
    val networkList: List<NetworkPoint>
)