package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class CreateServerInteractor : CoroutineUseCase<Boolean, CreateServerInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        return if (params!!.dropletId != null) {
            repository.createServer(params.dropletId!!, params.providerId, params.logCallback)
        } else {
            repository.createServer(
                params.providerId,
                params.regionSlug!!,
                params.typedConfig!!,
                params.isV2RayEnabled,
                params.logCallback
            )
        }
    }

    data class Params(
        val dropletId: Long? = null,
        val providerId: Long,
        val regionSlug: String? = null,
        val typedConfig: String? = null,
        val isV2RayEnabled: Boolean? = null,
        val logCallback: ProviderRepositoryImpl.LogCallback? = null
    )
}