package com.merseyside.dropletapp.providerApi

import com.merseyside.dropletapp.providerApi.cryptoServers.CryptoServersProvider
import com.merseyside.dropletapp.providerApi.linode.LinodeProvider
import com.merseyside.dropletapp.providerApi.digitalOcean.DigitalOceanProvider
import io.ktor.client.engine.HttpClientEngine

class  ProviderApiFactory(private val httpClientEngine: HttpClientEngine) {

    fun create(provider: Provider): ProviderApi {
        return when(provider) {
            is Provider.DigitalOcean -> {
                DigitalOceanProvider.getInstance(httpClientEngine)
            }

            is Provider.Linode -> {
                LinodeProvider.getInstance(httpClientEngine)
            }

            is Provider.CryptoServers -> {
                CryptoServersProvider.getInstance(httpClientEngine)
            }

            else -> {
                throw UnsupportedOperationException()
            }
        }
    }

    fun getProvider(providerId: Long): ProviderApi? {
        return when (Provider.getProviderById(providerId)) {
            is Provider.DigitalOcean -> {
                create(Provider.DigitalOcean())
            }

            is Provider.Linode -> {
                create(Provider.Linode())
            }

            is Provider.CryptoServers -> {
                create(Provider.CryptoServers())
            }

            else -> null
        }
    }
}