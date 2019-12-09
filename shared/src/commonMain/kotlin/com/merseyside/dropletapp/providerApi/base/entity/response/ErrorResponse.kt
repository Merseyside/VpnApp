package com.merseyside.dropletapp.providerApi.base.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error")
    val error: String
)