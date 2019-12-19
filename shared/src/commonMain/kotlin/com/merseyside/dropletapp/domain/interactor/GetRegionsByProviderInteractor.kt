package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import org.kodein.di.erased.instance

class GetRegionsByProviderInteractor : CoroutineUseCase<List<RegionPoint>, GetRegionsByProviderInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): List<RegionPoint> {
        return repository.getRegions(params!!.providerId)
    }

    data class Params(
        val providerId: Long
    )
}