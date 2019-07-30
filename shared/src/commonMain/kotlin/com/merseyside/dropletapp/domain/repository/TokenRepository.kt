package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token

interface TokenRepository {

    suspend fun getTokensByServiceId(serviceId: Long): List<TokenEntity>

    suspend fun addToken(token: Token, name: String, serviceId: Long): Boolean
}