package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.ServiceRepositoryImpl
import com.merseyside.dropletapp.domain.repository.ServiceRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private val serviceModule = Kodein.Module("service") {

    bind<ServiceRepository>() with singleton { ServiceRepositoryImpl() }
}

internal val serviceComponent = Kodein {
    extend(appComponent)
    import(serviceModule)
}