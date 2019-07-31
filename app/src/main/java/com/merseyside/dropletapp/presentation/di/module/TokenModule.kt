package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.GetServicesInteractor
import com.merseyside.dropletapp.presentation.view.fragment.token.model.TokenViewModel
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class TokenModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun addTokenFragmentViewModelProvider(
        router: Router,
        getServicesUseCase: GetServicesInteractor
    ): ViewModelProvider.Factory {
        return AddProfileFragmentViewModelProviderFactory(bundle, router, getServicesUseCase)
    }

    @Provides
    internal fun provideTokenProfileFragmentViewModel(factory: ViewModelProvider.Factory): TokenViewModel {
        return ViewModelProviders.of(fragment, factory).get(TokenViewModel::class.java)
    }

    class AddProfileFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getServicesUseCase: GetServicesInteractor
    ): BundleAwareViewModelFactory<TokenViewModel>(bundle) {

        override fun getViewModel(): TokenViewModel {
            return TokenViewModel(router, getServicesUseCase)
        }
    }
}