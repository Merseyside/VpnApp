package com.merseyside.dropletapp.providerApi.amazon.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AmazonImportKeyResponse(
    @SerialName("errorCode")
    val errorCode: String? = null,

    @SerialName("errorDetails")
    val errorDetails: String? = null,

    @SerialName("status")
    val status: String
)