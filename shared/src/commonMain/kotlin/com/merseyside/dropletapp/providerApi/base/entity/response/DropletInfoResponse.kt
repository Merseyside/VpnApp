package com.merseyside.dropletapp.providerApi.base.entity.response

import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint

data class DropletInfoResponse(
    val id: Long,
    val name: String,
    val status: String,
    val createdAt: String,
    val regionName: String,
    val networks: List<NetworkPoint>
)