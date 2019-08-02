package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private val providerModule = Kodein.Module("service") {

    bind<ProviderRepository>() with singleton { ProviderRepositoryImpl( instance() ) }
}

internal val providerComponent = Kodein {
    extend(appComponent)
    import(providerModule)
}