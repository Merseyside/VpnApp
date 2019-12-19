package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.domain.interactor.GetOvpnFileInteractor
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.model.DropletViewModel
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseFragment
import com.merseyside.mvvmcleanarch.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class DropletModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun addDropletViewModelProvider(
        router: Router,
        getDropletsUseCase: GetDropletsInteractor,
        getOvpnFileUseCase: GetOvpnFileInteractor,
        createServerUseCase: CreateServerInteractor
    ): ViewModelProvider.Factory {
        return DropletViewModelProviderFactory(bundle, router, getDropletsUseCase, getOvpnFileUseCase, createServerUseCase)
    }

    @Provides
    internal fun provideCreateServerInteractor(): CreateServerInteractor {
        return CreateServerInteractor()
    }

    @Provides
    internal fun provideDropletViewModel(factory: ViewModelProvider.Factory): DropletViewModel {
        return ViewModelProviders.of(fragment, factory).get(DropletViewModel::class.java)
    }

    @Provides
    internal fun provideGetDropletsInteractor(): GetDropletsInteractor {
        return GetDropletsInteractor()
    }

    @Provides
    internal fun provideGetOvpnFileInteractor(): GetOvpnFileInteractor {
        return GetOvpnFileInteractor()
    }

    class DropletViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getDropletsUseCase: GetDropletsInteractor,
        private val getOvpnFileUseCase: GetOvpnFileInteractor,
        private val createServerUseCase: CreateServerInteractor
    ): BundleAwareViewModelFactory<DropletViewModel>(bundle) {

        override fun getViewModel(): DropletViewModel {
            return DropletViewModel(router, getDropletsUseCase, getOvpnFileUseCase, createServerUseCase)
        }
    }
}