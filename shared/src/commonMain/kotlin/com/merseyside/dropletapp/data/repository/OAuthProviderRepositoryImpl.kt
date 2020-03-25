package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.domain.model.OAuthConfig
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import com.merseyside.dropletapp.domain.repository.TokenRepository
import com.merseyside.dropletapp.providerApi.Provider

class OAuthProviderRepositoryImpl(
    private val tokenRepository: TokenRepository
) : OAuthProviderRepository {


    override suspend fun getOAuthProviders(): List<OAuthProvider> {
        val providers = ProviderRepositoryImpl.providers

        return providers.map {provider ->

            OAuthProvider(
                provider,
                getTokenByProvider(provider),
                OAuthConfig.Builder(getConfigFileName(provider)).build()
            )
        }
    }

    private suspend fun getTokenByProvider(provider: Provider): String? {
        val tokens = tokenRepository.getTokensByProviderId(provider.getId())

        return if (tokens.isNotEmpty()) {
            tokens.first().token
        } else {
            null
        }
    }

    override suspend fun getOAuthProvider(provider: Provider): OAuthProvider {
        getOAuthProviders().forEach {
            if (it.provider.getId() == provider.getId()) {
                return it
            }
        }

        throw IllegalStateException()
    }

    private fun getConfigFileName(provider: Provider): String {
        return when(provider) {
            is Provider.Linode -> "linode.json"
            is Provider.CryptoServers -> "crypto_servers.json"
            is Provider.DigitalOcean -> "digital_ocean.json"
            is Provider.Custom -> ""
        }
    }
}