package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.providerApi.amazon.AmazonProvider
import com.merseyside.dropletapp.providerApi.digitalOcean.DigitalOceanProvider
import io.ktor.client.engine.HttpClientEngine

class  ProviderApiFactory(private val httpClientEngine: HttpClientEngine) {

    fun create(provider: Provider): ProviderApi {
        return when(provider) {
            is Provider.DigitalOcean -> {
                DigitalOceanProvider.getInstance(httpClientEngine)
            }

            is Provider.Amazon -> {
                AmazonProvider.getInstance(httpClientEngine)
            }
        }
    }

    fun getProvider(providerId: Long): ProviderApi {
        return when (Provider.getProviderById(providerId)) {
            is Provider.DigitalOcean -> {
                create(Provider.DigitalOcean())
            }

            is Provider.Amazon -> {
                create(Provider.Amazon())
            }

            else -> throw IllegalArgumentException()
        }
    }
}