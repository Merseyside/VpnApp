package com.merseyside.dropletapp.domain.repository

interface FreeAccessRepository {

    suspend fun getVpnConfig(typeName: String): String
}