package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class DeleteDropletInteractor : CoroutineUseCase<Boolean, DeleteDropletInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return repository.deleteDroplet(params!!.providerId, params.dropletId)
    }

    data class Params(
        val providerId: Long,
        val dropletId: Long
    )
}