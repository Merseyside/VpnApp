package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint

interface TokenRepository {

    suspend fun getTokensByProviderId(providerId: Long): List<TokenEntity>

    suspend fun addToken(token: Token, providerId: Long): Boolean

    suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint>

    suspend fun getAllTokens(): List<TokenEntity>

    suspend fun deleteToken(token: Token): Boolean
}