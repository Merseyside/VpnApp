package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.DropletPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanCreateDropletResponse(

    @SerialName("droplet")
    val dropletPoint: DropletPoint
)