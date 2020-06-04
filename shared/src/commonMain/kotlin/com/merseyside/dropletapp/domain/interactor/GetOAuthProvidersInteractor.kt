package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.authProviderComponent
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import org.kodein.di.erased.instance

class GetOAuthProvidersInteractor : CoroutineUseCase<List<OAuthProvider>, Any>() {

    private val repository: OAuthProviderRepository by authProviderComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<OAuthProvider> {
        return repository.getOAuthProviders()
    }
}