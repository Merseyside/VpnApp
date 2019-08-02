package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.base.entity.response.CreateDropletResponse
import kotlinx.serialization.Serializable

@Serializable
data class DigitalOceanCreateDropletResponse(
    override val id: Long,
    override val name: String

) : CreateDropletResponse()