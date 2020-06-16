package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.connectionTypes.Type

interface FreeAccessRepository {

    suspend fun getVpnConfig(type: Type): String
}