package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.di.tokenComponent
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.erased.instance

class SaveTokenInteractor : CoroutineUseCase<Boolean, SaveTokenInteractor.Params>() {

    private val repository: TokenRepository by tokenComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return repository.addToken(params!!.token, params.providerId)
    }

    data class Params(
        val token: Token,
        val providerId: Long
    )
}