package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.db.token.TokenDao
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.entity.service.Service
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val tokenDao: TokenDao
) : TokenRepository {

    override suspend fun getTokensByServiceId(serviceId: Long): List<TokenEntity> {
        return tokenDao.selectByServiceId(serviceId)
    }

    override suspend fun addToken(token: Token, name: String, serviceId: Long): Boolean {
        tokenDao.insert(token, name, Service.getServiceById(serviceId) ?: throw NoDataException("No service with passed id"))

        return true
    }

    companion object {

        private const val TAG = "TokenRepository"
    }
}