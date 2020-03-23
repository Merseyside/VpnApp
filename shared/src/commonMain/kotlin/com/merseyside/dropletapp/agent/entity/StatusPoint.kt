package com.merseyside.dropletapp.agent.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusPoint(
    @SerialName("code")
    val code: String,

    @SerialName("client_config")
    val config: String,

    @SerialName("error_text")
    val errorMsg: String
)