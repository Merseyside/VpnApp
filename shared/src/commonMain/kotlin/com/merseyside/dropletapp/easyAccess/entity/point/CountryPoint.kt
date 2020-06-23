package com.merseyside.dropletapp.easyAccess.entity.point

import kotlinx.serialization.Serializable

@Serializable
data class CountryPoint(
    val id: String,
    val name: String,
    val alpha2: String,
    val alpha3: String
)