package com.merseyside.dropletapp.freeAccess.entity.point

import kotlinx.serialization.Serializable

@Serializable
data class DataPoint(
    val access: AccessPoint
)