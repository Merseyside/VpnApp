package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.di.tokenComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.erased.instance

class DeleteTokenInteractor : CoroutineUseCase<Boolean, DeleteTokenInteractor.Params>() {

    private val repository: TokenRepository by tokenComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return repository.deleteToken(params!!.token)
    }

    data class Params(
        val token: Token
    )
}