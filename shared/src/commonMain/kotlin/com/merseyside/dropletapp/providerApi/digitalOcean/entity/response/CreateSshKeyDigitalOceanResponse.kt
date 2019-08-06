package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.SshKeyPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSshKeyDigitalOceanResponse(

    @SerialName("ssh_key")
    val sshKeyPoint: SshKeyPoint
)