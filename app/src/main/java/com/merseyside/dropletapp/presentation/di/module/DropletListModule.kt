package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.GetServersInteractor
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletListViewModel
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class DropletListModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideGetServersInteractor(): GetServersInteractor {
        return GetServersInteractor()
    }

    @Provides
    internal fun provideDropletListViewModelProvider(
        router: Router,
        getServersUseCase: GetServersInteractor
    ): ViewModelProvider.Factory {
        return DropletListViewModelProviderFactory(bundle, router, getServersUseCase)
    }

    @Provides
    internal fun provideDropletListViewModel(factory: ViewModelProvider.Factory): DropletListViewModel {
        return ViewModelProviders.of(fragment, factory).get(DropletListViewModel::class.java)
    }

    class DropletListViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getServersUseCase: GetServersInteractor
    ): BundleAwareViewModelFactory<DropletListViewModel>(bundle) {

        override fun getViewModel(): DropletListViewModel {
            return DropletListViewModel(router, getServersUseCase)
        }
    }
}