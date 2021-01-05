package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.*
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model.AddDropletViewModel
import com.merseyside.archy.presentation.fragment.BaseFragment
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
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
    internal fun provideGetRegionsByTokenInteractor(): GetRegionsByProviderInteractor {
        return GetRegionsByProviderInteractor()
    }

    @Provides
    internal fun provideGetTypedConfigsInteractor(): GetTypedConfigNamesInteractor {
        return GetTypedConfigNamesInteractor()
    }

    @Provides
    internal fun provideAddProviderFragmentViewModel(factory: ViewModelProvider.Factory): AddDropletViewModel {
        return ViewModelProvider(fragment, factory).get(AddDropletViewModel::class.java)
    }

    @Provides
    internal fun provideAddProviderFragmentViewModelProvider(
        application: Application,
        router: Router,
        getRegionsByProviderUseCase: GetRegionsByProviderInteractor,
        createServerUseCase: CreateServerInteractor,
        getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
    ): ViewModelProvider.Factory {
        return AddProviderFragmentViewModelProviderFactory(
            bundle,
            application,
            router,
            getRegionsByProviderUseCase,
            createServerUseCase,
            getTypedConfigNamesUseCase
        )
    }

    class AddProviderFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val router: Router,
        private val getRegionsByProviderUseCase: GetRegionsByProviderInteractor,
        private val createServerUseCase: CreateServerInteractor,
        private val getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
    ): BundleAwareViewModelFactory<AddDropletViewModel>(bundle) {

        override fun getViewModel(): AddDropletViewModel {
            return AddDropletViewModel(
                application,
                router,
                getRegionsByProviderUseCase,
                createServerUseCase,
                getTypedConfigNamesUseCase
            )
        }
    }
}