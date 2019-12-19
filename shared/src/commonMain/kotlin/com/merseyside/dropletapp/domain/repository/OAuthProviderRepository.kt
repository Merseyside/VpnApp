package com.merseyside.dropletapp.domain.repository

import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.providerApi.Provider

interface OAuthProviderRepository {
    suspend fun getOAuthProviders(): List<OAuthProvider>

    suspend fun getOAuthProvider(provider: Provider): OAuthProvider

}