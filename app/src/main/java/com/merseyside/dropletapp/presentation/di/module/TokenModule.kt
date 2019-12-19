package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.SaveTokenInteractor
import com.merseyside.dropletapp.presentation.view.fragment.token.model.TokenViewModel
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import com.merseyside.mvvmcleanarch.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class TokenModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideSaveTokenInteractor(): SaveTokenInteractor {
        return SaveTokenInteractor()
    }

    @Provides
    internal fun provideTokenFragmentViewModelProvider(
        router: Router,
        getProvidersUseCase: GetProvidersInteractor,
        saveTokenUseCase: SaveTokenInteractor
    ): ViewModelProvider.Factory {
        return TokenFragmentViewModelProviderFactory(bundle, router, getProvidersUseCase, saveTokenUseCase)
    }

    @Provides
    internal fun provideTokenFragmentViewModel(factory: ViewModelProvider.Factory): TokenViewModel {
        return ViewModelProviders.of(fragment, factory).get(TokenViewModel::class.java)
    }

    class TokenFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getProvidersUseCase: GetProvidersInteractor,
        private val saveTokenUseCase: SaveTokenInteractor
    ): BundleAwareViewModelFactory<TokenViewModel>(bundle) {

        override fun getViewModel(): TokenViewModel {
            return TokenViewModel(router, getProvidersUseCase, saveTokenUseCase)
        }
    }
}