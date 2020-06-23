package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint

interface EasyAccessRepository {

    suspend fun getVpnConfig(
        type: Type,
        regionId: String
    ): String

    suspend fun getFreeVpnConfig(type: Type, regionId: String): String

    suspend fun getRegions(): List<RegionPoint>
}