package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

class ProviderRepositoryImpl(
    private val providerApiFactory: ProviderApiFactory
) : ProviderRepository {


    override suspend fun getServices(): List<Provider> {
        return providers
    }

    override suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint> {
        val provider = providerApiFactory.getProvider(providerId)

        return provider.getRegions(token)
    }

    companion object {

        private var providers: List<Provider> = Provider.getAllServices()

    }
}