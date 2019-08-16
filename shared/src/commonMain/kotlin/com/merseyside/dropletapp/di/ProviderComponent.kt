package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.kodein.di.erased.with

private val providerModule = Kodein.Module("providerName") {

    bind<ProviderRepository>() with singleton { ProviderRepositoryImpl( instance(), instance(), instance(), instance() ) }

    bind<SshManager>() with singleton { SshManager( instance("sshTimeout")) }

    bind<KeyDao>() with singleton { KeyDao( instance() ) }

    bind<ServerDao>() with singleton { ServerDao( instance() ) }

    constant("sshTimeout") with 3000
}

internal val providerComponent = Kodein {
    extend(appComponent)
    import(providerModule)
}