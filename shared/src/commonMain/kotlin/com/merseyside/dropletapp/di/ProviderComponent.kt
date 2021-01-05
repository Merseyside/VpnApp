package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.agent.net.Agent
import com.merseyside.dropletapp.data.cipher.AesCipher
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import org.kodein.di.*

private val providerModule = DI.Module("providerName") {

    bind<ProviderRepository>() with singleton { ProviderRepositoryImpl(
        instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance())
    }

    bind<SshManager>() with singleton { SshManager(instance("sshTimeout")) }

    bind<KeyDao>() with singleton { KeyDao(instance()) }

    bind<ServerDao>() with singleton { ServerDao(instance()) }

    bind<Agent>() with singleton { Agent(instance(), instance()) }

    bind<AesCipher>() with singleton { AesCipher() }

    constant("sshTimeout") with 3000
}

internal val providerComponent = DI {
    extend(appComponent)
    import(providerModule)
    import(tokenModule)
    import(authProviderModule)
}