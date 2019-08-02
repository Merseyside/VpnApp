package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.providerApi.digitalOcean.DigitalOceanProvider
import io.ktor.client.engine.HttpClientEngine

class ProviderApiFactory(private val httpClientEngine: HttpClientEngine) {

    fun create(provider: Provider): ProviderApi {
        when(provider) {
            is Provider.DigitalOcean -> {
                return DigitalOceanProvider.getInstance(httpClientEngine)
            }
        }
    }

    fun getProvider(providerId: Long): ProviderApi {
        return when (Provider.getProviderById(providerId)) {
            is Provider.DigitalOcean -> {
                create(Provider.DigitalOcean())
            }

            else -> throw IllegalArgumentException()
        }
    }
}