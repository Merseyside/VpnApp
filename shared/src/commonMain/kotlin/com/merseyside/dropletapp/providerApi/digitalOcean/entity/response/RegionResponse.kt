package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionResponse(

    @SerialName("regions")
    val regionList: List<RegionPoint>
)