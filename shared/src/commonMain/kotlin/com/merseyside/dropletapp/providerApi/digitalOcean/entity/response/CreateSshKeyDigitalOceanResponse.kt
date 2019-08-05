package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSshKeyDigitalOceanResponse(

    @SerialName("id")
    val id: Long,

    @SerialName("fingerprint")
    val fingerprint: String,

    @SerialName("public_key")
    val publicKey: String,

    @SerialName("name")
    val name: String
)