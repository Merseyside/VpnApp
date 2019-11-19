package com.merseyside.dropletapp.providerApi.cryptoServers.entity.point

import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoDropletInfoPoint(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("status")
    val status: String,

    @SerialName("region")
    val regionPoint: RegionPoint,

    @SerialName("networks")
    val networkList: List<CryptoNetworkPoint>
)
