package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.db.token.TokenDao
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.domain.repository.TokenRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

class TokenRepositoryImpl(
    private val tokenDao: TokenDao,
    private val providerApiFactory: ProviderApiFactory
) : TokenRepository {

    override suspend fun getTokensByProviderId(providerId: Long): List<TokenEntity> {
        return tokenDao.selectByServiceId(providerId)
    }

    override suspend fun addToken(token: Token, name: String, providerId: Long): Boolean {
        val provider = providerApiFactory.getProvider(providerId)

        return if (provider.isTokenValid(token)) {
            tokenDao.insert(token, name, Provider.getProviderById(providerId) ?: throw NoDataException("No service with passed id"))

            true
        } else {
            false
        }
    }

    override suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint> {
        val provider = providerApiFactory.getProvider(providerId)

        return provider.getRegions(token)
    }

    companion object {

        private const val TAG = "TokenRepository"
    }
}