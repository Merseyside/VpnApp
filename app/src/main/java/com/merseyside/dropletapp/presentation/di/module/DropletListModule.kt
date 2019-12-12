package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetOvpnFileInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
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
    internal fun provideCreateServerInteractor(): CreateServerInteractor {
        return CreateServerInteractor()
    }

    @Provides
    internal fun provideOvpnFileInteractor(): GetOvpnFileInteractor {
        return GetOvpnFileInteractor()
    }

    @Provides
    internal fun provideDeleteDropletInteractor(): DeleteDropletInteractor {
        return DeleteDropletInteractor()
    }

    @Provides
    internal fun provideGetServersInteractor(): GetDropletsInteractor {
        return GetDropletsInteractor()
    }

    @Provides
    internal fun provideDropletListViewModelProvider(
        router: Router,
        getDropletsUseCase: GetDropletsInteractor,
        deleteDropletUseCase: DeleteDropletInteractor,
        getOvpnFileUseCase: GetOvpnFileInteractor,
        createServerUseCase: CreateServerInteractor
    ): ViewModelProvider.Factory {
        return DropletListViewModelProviderFactory(
            bundle,
            router,
            getDropletsUseCase,
            deleteDropletUseCase,
            getOvpnFileUseCase,
            createServerUseCase
        )
    }

    @Provides
    internal fun provideDropletListViewModel(factory: ViewModelProvider.Factory): DropletListViewModel {
        return ViewModelProviders.of(fragment, factory).get(DropletListViewModel::class.java)
    }

    class DropletListViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getDropletsUseCase: GetDropletsInteractor,
        private val deleteDropletUseCase: DeleteDropletInteractor,
        private val getOvpnFileUseCase: GetOvpnFileInteractor,
        private val createServerUseCase: CreateServerInteractor
    ): BundleAwareViewModelFactory<DropletListViewModel>(bundle) {

        override fun getViewModel(): DropletListViewModel {
            return DropletListViewModel(
                router,
                getDropletsUseCase,
                deleteDropletUseCase,
                getOvpnFileUseCase,
                createServerUseCase
            )
        }
    }
}