package com.merseyside.dropletapp.agent.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(

    @SerialName("status")
    val status: StatusPoint
)