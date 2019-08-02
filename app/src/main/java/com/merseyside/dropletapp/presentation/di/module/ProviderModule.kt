package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.GetRegionsByTokenInteractor
import com.merseyside.dropletapp.domain.interactor.GetTokensByProviderIdInteractor
import com.merseyside.dropletapp.presentation.view.fragment.provider.model.ProviderViewModel
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class ProviderModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideGetTokensByIdInteractor(): GetTokensByProviderIdInteractor {
        return GetTokensByProviderIdInteractor()
    }

    @Provides
    internal fun provideGetRegionsByTokenInteractor(): GetRegionsByTokenInteractor {
        return GetRegionsByTokenInteractor()
    }

    @Provides
    internal fun provideProviderFragmentViewModelProvider(
        router: Router,
        getProvidersUseCase: GetProvidersInteractor,
        getTokensByProviderIdUseCase: GetTokensByProviderIdInteractor,
        getRegionsByTokenUseCase: GetRegionsByTokenInteractor
    ): ViewModelProvider.Factory {
        return ProviderFragmentViewModelProviderFactory(
            bundle,
            router,
            getProvidersUseCase,
            getTokensByProviderIdUseCase,
            getRegionsByTokenUseCase
        )
    }

    @Provides
    internal fun provideProviderFragmentViewModel(factory: ViewModelProvider.Factory): ProviderViewModel {
        return ViewModelProviders.of(fragment, factory).get(ProviderViewModel::class.java)
    }

    class ProviderFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getProvidersUseCase: GetProvidersInteractor,
        private val getTokensByProviderIdUseCase: GetTokensByProviderIdInteractor,
        private val getRegionsByTokenUseCase: GetRegionsByTokenInteractor
    ): BundleAwareViewModelFactory<ProviderViewModel>(bundle) {

        override fun getViewModel(): ProviderViewModel {
            return ProviderViewModel(router, getProvidersUseCase, getTokensByProviderIdUseCase, getRegionsByTokenUseCase)
        }
    }
}