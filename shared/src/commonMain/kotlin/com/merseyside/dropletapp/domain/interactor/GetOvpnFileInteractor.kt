package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class GetOvpnFileInteractor : CoroutineUseCase<String, GetOvpnFileInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): String {
        return repository.getOvpnFile(params!!.dropletId, params.providerId)
    }

    data class Params(
        val dropletId: Long,
        val providerId: Long
    )
}