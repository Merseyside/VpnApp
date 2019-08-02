package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.di.tokenComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.erased.instance

class GetTokensByProviderIdInteractor : CoroutineUseCase<List<TokenEntity>, GetTokensByProviderIdInteractor.Params>() {

    private val repository: TokenRepository by tokenComponent.instance()

    override suspend fun executeOnBackground(params: Params?): List<TokenEntity> {
        return repository.getTokensByProviderId(params!!.id)
    }

    data class Params(
        val id: Long
    )
}