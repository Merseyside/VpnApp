package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetProvidersInteractor
import com.merseyside.dropletapp.domain.interactor.GetRegionsByTokenInteractor
import com.merseyside.dropletapp.domain.interactor.GetTokensByProviderIdInteractor
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model.AddDropletViewModel
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class AddDropletModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideCreateServerInteractor(): CreateServerInteractor {
        return CreateServerInteractor()
    }

    @Provides
    internal fun provideGetTokensByIdInteractor(): GetTokensByProviderIdInteractor {
        return GetTokensByProviderIdInteractor()
    }

    @Provides
    internal fun provideGetRegionsByTokenInteractor(): GetRegionsByTokenInteractor {
        return GetRegionsByTokenInteractor()
    }

    @Provides
    internal fun provideAddProviderFragmentViewModelProvider(
        router: Router,
        getProvidersUseCase: GetProvidersInteractor,
        getTokensByProviderIdUseCase: GetTokensByProviderIdInteractor,
        getRegionsByTokenUseCase: GetRegionsByTokenInteractor,
        createServerUseCase: CreateServerInteractor
    ): ViewModelProvider.Factory {
        return AddProviderFragmentViewModelProviderFactory(
            bundle,
            router,
            getProvidersUseCase,
            getTokensByProviderIdUseCase,
            getRegionsByTokenUseCase,
            createServerUseCase
        )
    }

    @Provides
    internal fun provideAddProviderFragmentViewModel(factory: ViewModelProvider.Factory): AddDropletViewModel {
        return ViewModelProviders.of(fragment, factory).get(AddDropletViewModel::class.java)
    }

    class AddProviderFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getProvidersUseCase: GetProvidersInteractor,
        private val getTokensByProviderIdUseCase: GetTokensByProviderIdInteractor,
        private val getRegionsByTokenUseCase: GetRegionsByTokenInteractor,
        private val createServerUseCase: CreateServerInteractor
    ): BundleAwareViewModelFactory<AddDropletViewModel>(bundle) {

        override fun getViewModel(): AddDropletViewModel {
            return AddDropletViewModel(
                router,
                getProvidersUseCase,
                getTokensByProviderIdUseCase,
                getRegionsByTokenUseCase,
                createServerUseCase)
        }
    }
}