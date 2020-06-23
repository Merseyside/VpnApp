package com.merseyside.dropletapp.easyAccess.entity.point

import kotlinx.serialization.Serializable

@Serializable
data class RegionPoint(
    val available: Boolean,
    val country: CountryPoint,
    val services: ServicePoint
)