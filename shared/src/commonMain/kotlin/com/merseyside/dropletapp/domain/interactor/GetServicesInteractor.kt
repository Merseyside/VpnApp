package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.service.Service
import com.merseyside.dropletapp.di.serviceComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ServiceRepository
import org.kodein.di.erased.instance

class GetServicesInteractor : CoroutineUseCase<List<Service>, Any>() {

    private val repository: ServiceRepository by serviceComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<Service> {
        return repository.getServices()
    }
}