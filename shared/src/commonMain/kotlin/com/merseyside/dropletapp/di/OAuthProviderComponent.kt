package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.OAuthProviderRepositoryImpl
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import com.merseyside.dropletapp.utils.AccountManager
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

expect var accountManager: AccountManager?

internal val authProviderModule = Kodein.Module("authProvider") {

    bind<OAuthProviderRepository>() with singleton { OAuthProviderRepositoryImpl( instance() ) }
}

internal val authProviderComponent = Kodein {
    extend(appComponent)

    import(authProviderModule)
    import(tokenModule)
}