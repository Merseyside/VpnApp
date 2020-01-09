package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.CreateServerInteractor
import com.merseyside.dropletapp.domain.interactor.DeleteDropletInteractor
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
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
        createServerUseCase: CreateServerInteractor,
        deleteServerUseCase: DeleteDropletInteractor
    ): ViewModelProvider.Factory {
        return DropletViewModelProviderFactory(
            bundle,
            router,
            getDropletsUseCase,
            createServerUseCase,
            deleteServerUseCase
        )
    }

    @Provides
    internal fun provideDeleteDropletInteractor(): DeleteDropletInteractor {
        return DeleteDropletInteractor()
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

    class DropletViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getDropletsUseCase: GetDropletsInteractor,
        private val createServerUseCase: CreateServerInteractor,
        private val deleteServerUseCase: DeleteDropletInteractor
    ): BundleAwareViewModelFactory<DropletViewModel>(bundle) {

        override fun getViewModel(): DropletViewModel {
            return DropletViewModel(router, getDropletsUseCase, createServerUseCase, deleteServerUseCase)
        }
    }
}