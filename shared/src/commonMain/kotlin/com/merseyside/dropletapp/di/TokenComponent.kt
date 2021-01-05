package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.db.token.TokenDao
import com.merseyside.dropletapp.data.repository.TokenRepositoryImpl
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val tokenModule = DI.Module("token") {

    bind<TokenDao>() with singleton { TokenDao( instance() ) }

    bind<TokenRepository>() with singleton { TokenRepositoryImpl(instance(), instance()) }
}

internal val tokenComponent = DI {
    extend(appComponent)

    import(tokenModule)
}