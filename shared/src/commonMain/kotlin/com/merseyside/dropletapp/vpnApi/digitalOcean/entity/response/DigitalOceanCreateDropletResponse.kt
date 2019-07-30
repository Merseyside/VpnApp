package com.merseyside.dropletapp.vpnApi.digitalOcean.entity.response

import com.merseyside.dropletapp.vpnApi.base.entity.response.CreateDropletResponse
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanCreateDropletResponse(
    override val id: Long,
    override val name: String

) : CreateDropletResponse()