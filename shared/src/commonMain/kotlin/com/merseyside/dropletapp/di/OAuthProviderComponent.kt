package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.OAuthProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import com.merseyside.dropletapp.utils.AccountManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

expect var accountManager: AccountManager?

internal val authProviderModule = DI.Module("authProvider") {

    bind<OAuthProviderRepository>() with singleton { OAuthProviderRepositoryImpl( instance() ) }
}

internal val authProviderComponent = DI {
    extend(appComponent)

    import(authProviderModule)
    import(tokenModule)
}