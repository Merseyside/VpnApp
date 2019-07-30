package com.merseyside.dropletapp.vpnApi

interface VpnApi {

    suspend fun isTokenValid(): Boolean

    suspend fun createDroplet()
}