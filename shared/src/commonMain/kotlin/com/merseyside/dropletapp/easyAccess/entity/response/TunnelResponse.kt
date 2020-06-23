package com.merseyside.dropletapp.easyAccess.entity.response

import com.merseyside.dropletapp.easyAccess.entity.point.DataPoint
import kotlinx.serialization.Serializable

@Serializable
data class TunnelResponse(
    val code: Int,
    val message: String,
    val data: DataPoint
)