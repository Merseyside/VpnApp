package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.Provider
import org.kodein.di.instance

class GetProvidersWithTokenInteractor : CoroutineUseCase<List<Provider>, Any>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<Provider> {
        return repository.getProvidersWithToken()
    }
}