package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.db.token.TokenDao
import com.merseyside.dropletapp.data.repository.TokenRepositoryImpl
import com.merseyside.dropletapp.domain.repository.TokenRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

internal val tokenModule = Kodein.Module("token") {

    bind<TokenDao>() with singleton { TokenDao( instance() ) }

    bind<TokenRepository>() with singleton { TokenRepositoryImpl( instance() ) }
}

internal val tokenComponent = Kodein {
    extend(appComponent)

    import(tokenModule)
}