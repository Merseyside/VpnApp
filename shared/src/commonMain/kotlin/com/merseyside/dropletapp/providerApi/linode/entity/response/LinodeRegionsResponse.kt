package com.merseyside.dropletapp.providerApi.linode.entity.response

import com.merseyside.dropletapp.providerApi.linode.entity.points.LinodeRegionPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinodeRegionsResponse(
    @SerialName("data")
    val regionPoint: List<LinodeRegionPoint>
)