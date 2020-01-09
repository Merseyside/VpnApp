package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.content.Context
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.mvvmcleanarch.presentation.di.qualifiers.ApplicationContext
import com.merseyside.mvvmcleanarch.utils.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    @ApplicationContext
    fun provideContext() : Context {
        return application
    }

    @Provides
    fun provideApplication() : Application {
        return application
    }

    @Provides
    internal fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager.Builder()
            .setContext(context)
            .setShared(false)
            .build()
    }

    @Provides
    internal fun provideDatabaseName(): String {
        return "database.db"
    }

    @Provides
    fun provideGetServicesInteractor(): GetProvidersInteractor {
        return GetProvidersInteractor()
    }
}