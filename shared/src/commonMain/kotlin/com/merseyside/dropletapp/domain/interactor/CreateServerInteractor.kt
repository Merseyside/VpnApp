package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class CreateServerInteractor : CoroutineUseCase<Boolean, CreateServerInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return repository.createServer(params!!.token, params.providerId, params.regionSlug, params.serverName)
    }

    data class Params(
        val token: Token,
        val providerId: Long,
        val regionSlug: String,
        val serverName: String
    )
}