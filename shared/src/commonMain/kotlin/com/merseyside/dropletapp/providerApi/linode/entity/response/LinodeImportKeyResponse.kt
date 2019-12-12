package com.merseyside.dropletapp.providerApi.linode.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinodeImportKeyResponse(
    @SerialName("id")
    val id: Long,

   @SerialName("label")
   val label: String
)