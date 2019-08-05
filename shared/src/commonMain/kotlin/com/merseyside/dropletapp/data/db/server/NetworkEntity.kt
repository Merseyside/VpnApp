package com.merseyside.dropletapp.data.db.server

import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint
import kotlinx.serialization.Serializable

@Serializable
data class NetworkEntity(
    val networkList: List<NetworkPoint>
)