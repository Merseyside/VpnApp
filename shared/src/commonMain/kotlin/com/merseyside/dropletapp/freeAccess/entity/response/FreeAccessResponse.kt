package com.merseyside.dropletapp.freeAccess.entity.response

import com.merseyside.dropletapp.freeAccess.entity.point.DataPoint
import kotlinx.serialization.Serializable

@Serializable
data class FreeAccessResponse(
    val code: Int,
    val message: String,
    val data: DataPoint
)