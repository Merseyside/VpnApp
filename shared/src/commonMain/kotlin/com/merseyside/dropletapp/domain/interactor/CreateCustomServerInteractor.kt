package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.erased.instance

class CreateCustomServerInteractor : CoroutineUseCase<Boolean, CreateCustomServerInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): Boolean {
        params!!.apply {
            return repository.createCustomServer(typeName, userName, host, port, password, sshKey, isV2RayEnabled)
        }
    }

    data class Params(
        val typeName: String,
        val userName: String,
        val host: String,
        val port: Int,
        val password: String?,
        val sshKey: String?,
        val isV2RayEnabled: Boolean?,
        val logCallback: ProviderRepositoryImpl.LogCallback? = null
    )
}