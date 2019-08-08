package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class GetServersInteractor : CoroutineUseCase<List<Server>, Any>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<Server> {
        return repository.getServers()
    }
}