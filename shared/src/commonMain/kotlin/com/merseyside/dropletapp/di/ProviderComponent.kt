package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.cipher.RsaManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

private val providerModule = Kodein.Module("provider") {

    bind<ProviderRepository>() with singleton { ProviderRepositoryImpl( instance(), instance(), instance(), instance() ) }

    bind<RsaManager>() with singleton { RsaManager() }

    bind<KeyDao>() with singleton { KeyDao( instance() ) }

    bind<ServerDao>() with singleton { ServerDao( instance() )}
}

internal val providerComponent = Kodein {
    extend(appComponent)
    import(providerModule)
}