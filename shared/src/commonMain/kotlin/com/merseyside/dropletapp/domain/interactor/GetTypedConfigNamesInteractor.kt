package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class GetTypedConfigNamesInteractor : CoroutineUseCase<List<String>, Any>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<String> {
        return repository.getTypedConfigNames()
    }
}