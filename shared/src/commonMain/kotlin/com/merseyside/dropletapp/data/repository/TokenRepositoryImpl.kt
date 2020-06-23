package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.db.token.TokenDao
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.domain.repository.TokenRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.kmpMerseyLib.utils.Logger
import com.russhwolf.settings.Settings

class TokenRepositoryImpl(
    private val tokenDao: TokenDao,
    private val providerApiFactory: ProviderApiFactory
) : TokenRepository {

    override suspend fun getAllTokens(): List<TokenEntity> {
        return tokenDao.getAllTokens()
    }

    override suspend fun deleteToken(token: Token): Boolean {
        tokenDao.deleteToken(token)
        return true
    }

    override suspend fun getTokensByProviderId(providerId: Long): List<TokenEntity> {
        return tokenDao.selectByServiceId(providerId)
    }

    override suspend fun addToken(token: Token, providerId: Long): Boolean {
        tokenDao.insert(token,Provider.getProviderById(providerId) ?: throw NoDataException("No service with passed id"))

        return true
    }

    override suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint> {
        val provider = providerApiFactory.getProvider(providerId)

        return provider!!.getRegions(token)
    }

}