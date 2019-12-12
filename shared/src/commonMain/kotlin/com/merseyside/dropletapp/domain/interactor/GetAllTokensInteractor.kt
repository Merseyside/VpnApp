package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.di.tokenComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.erased.instance

class GetAllTokensInteractor : CoroutineUseCase<List<TokenEntity>, Any>() {

    private val repository: TokenRepository by tokenComponent.instance()


    override suspend fun executeOnBackground(params: Any?): List<TokenEntity> {
        return repository.getAllTokens()
    }
}