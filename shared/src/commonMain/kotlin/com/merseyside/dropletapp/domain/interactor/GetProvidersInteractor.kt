package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class GetProvidersInteractor : CoroutineUseCase<List<Provider>, Any>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<Provider> {
        return repository.getServices()
    }
}