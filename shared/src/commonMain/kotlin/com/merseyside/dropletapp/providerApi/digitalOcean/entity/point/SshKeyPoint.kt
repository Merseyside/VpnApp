package com.merseyside.dropletapp.providerApi.digitalOcean.entity.point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SshKeyPoint(

    @SerialName("id")
    val id: Long,

    @SerialName("fingerprint")
    val fingerprint: String,

    @SerialName("public_key")
    val publicKey: String,

    @SerialName("name")
    val name: String
)