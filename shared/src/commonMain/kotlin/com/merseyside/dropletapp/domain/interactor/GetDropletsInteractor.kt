package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.kmpMerseyLib.domain.coroutines.FlowUseCase
import kotlinx.coroutines.flow.Flow
import org.kodein.di.instance

class GetDropletsInteractor : FlowUseCase<List<Server>, Any>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override fun executeOnBackground(params: Any?): Flow<List<Server>> {
        return repository.getDropletsFlow()
    }
}