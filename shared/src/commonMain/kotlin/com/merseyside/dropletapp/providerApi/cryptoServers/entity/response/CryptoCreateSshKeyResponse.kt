package com.merseyside.dropletapp.providerApi.cryptoServers.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoCreateSshKeyResponse(

    @SerialName("ssh_key_id")
    val id: Long,

    @SerialName("error")
    val error: String? = ""

)