package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class CreateServerInteractor : CoroutineUseCase<Boolean, CreateServerInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return if (params!!.dropletId != null) {
            repository.createServer(params.dropletId!!, params.providerId, params.logCallback)
        } else {
            repository.createServer(
                params.token!!,
                params.providerId,
                params.regionSlug!!,
                params.serverName!!,
                params.logCallback
            )
        }
    }

    data class Params(
        val token: Token? = null,
        val dropletId: Long? = null,
        val providerId: Long,
        val regionSlug: String? = null,
        val serverName: String? = null,
        val logCallback: ProviderRepositoryImpl.LogCallback? = null
    )
}