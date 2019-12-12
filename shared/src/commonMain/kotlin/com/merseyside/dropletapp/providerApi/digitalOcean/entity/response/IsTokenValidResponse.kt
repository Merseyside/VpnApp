package com.merseyside.dropletapp.providerApi.digitalOcean.entity.response

import com.merseyside.dropletapp.providerApi.digitalOcean.entity.point.AccountDataPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IsTokenValidResponse(

    @SerialName("account")
    val accountDataPoint: AccountDataPoint? = null
)