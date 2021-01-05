package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.GetOAuthProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.SaveTokenInteractor
import com.merseyside.dropletapp.presentation.view.fragment.auth.model.AuthViewModel
import com.merseyside.archy.presentation.fragment.BaseFragment
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class AuthModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideSaveTokenInteractor(): SaveTokenInteractor {
        return SaveTokenInteractor()
    }

    @Provides
    internal fun provideGetOAuthProvidersInteractor(): GetOAuthProvidersInteractor {
        return GetOAuthProvidersInteractor()
    }

    @Provides
    internal fun provideAuthViewModelProvider(
        application: Application,
        router: Router,
        getOAuthProvidersUseCase: GetOAuthProvidersInteractor,
        saveTokenInteractor: SaveTokenInteractor
    ): ViewModelProvider.Factory {
        return AuthViewModelProviderFactory(bundle, application, router, getOAuthProvidersUseCase, saveTokenInteractor)
    }

    @Provides
    internal fun provideAuthViewModel(factory: ViewModelProvider.Factory): AuthViewModel {
        return ViewModelProvider(fragment, factory).get(AuthViewModel::class.java)
    }

    class AuthViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val router: Router,
        private val getOAuthProvidersUseCase: GetOAuthProvidersInteractor,
        private val saveTokenInteractor: SaveTokenInteractor
    ): BundleAwareViewModelFactory<AuthViewModel>(bundle) {

        override fun getViewModel(): AuthViewModel {
            return AuthViewModel(application, router, getOAuthProvidersUseCase, saveTokenInteractor)
        }
    }
}