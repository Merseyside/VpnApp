package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.DropletInfoPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanDropletInfoResponse(

    @SerialName("droplet")
    val dropletInfoPoint: DropletInfoPoint
)