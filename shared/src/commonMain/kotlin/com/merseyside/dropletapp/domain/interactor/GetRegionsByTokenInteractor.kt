package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.di.providerComponent
import com.merseyside.dropletapp.domain.base.CoroutineUseCase
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import org.kodein.di.erased.instance

class GetRegionsByTokenInteractor : CoroutineUseCase<List<RegionPoint>, GetRegionsByTokenInteractor.Params>() {

    private val repository: ProviderRepository by providerComponent.instance()

    override suspend fun executeOnBackground(params: Params?): List<RegionPoint> {
        return repository.getRegions(params!!.token, params.providerId)
    }

    data class Params(
        val token: Token,
        val providerId: Long
    )
}