package com.merseyside.dropletapp.easyAccess.entity.point

import kotlinx.serialization.Serializable

@Serializable
data class DataPoint(
    val access: AccessPoint,
    val network: Network
)