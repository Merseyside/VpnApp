package com.merseyside.dropletapp.providerApi.digitalOcean.entity.point

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropletInfoPoint(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdTime: String,

    @SerialName("region")
    val regionPoint: RegionPoint,

    @SerialName("networks")
    val networkList: Map<String, List<NetworkPoint>>
)