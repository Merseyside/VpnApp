package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.freeAccessComponent
import com.merseyside.dropletapp.domain.repository.FreeAccessRepository
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import org.kodein.di.erased.instance

class GetVpnConfigInteractor : CoroutineUseCase<String, GetVpnConfigInteractor.Params>() {

    private val repository: FreeAccessRepository by freeAccessComponent.instance()

    data class Params(
        val typeName: String
    )

    override suspend fun executeOnBackground(params: Params?): String {
        return repository.getVpnConfig(params!!.typeName)
    }
}