package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.CreateCustomServerInteractor
import com.merseyside.dropletapp.domain.interactor.GetTypedConfigNamesInteractor
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addCustomServer.model.AddCustomDropletViewModel
import com.merseyside.archy.presentation.fragment.BaseFragment
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class AddCustomDropletModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
)  {

    @Provides
    internal fun provideAddProviderFragmentViewModel(factory: ViewModelProvider.Factory): AddCustomDropletViewModel {
        return ViewModelProvider(fragment, factory).get(AddCustomDropletViewModel::class.java)
    }

    @Provides
    internal fun provideAddCustomDropletViewModelProvider(
        application: Application,
        router: Router,
        createCustomServerUseCase: CreateCustomServerInteractor,
        getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
    ): ViewModelProvider.Factory {
        return AddCustomDropletViewModelProviderFactory(
            bundle,
            application,
            router,
            createCustomServerUseCase,
            getTypedConfigNamesUseCase
        )
    }

    @Provides
    internal fun getCreateCustomServerInteractor(): CreateCustomServerInteractor {
        return CreateCustomServerInteractor()
    }

    @Provides
    internal fun provideGetTypedConfigNamesInteractor(): GetTypedConfigNamesInteractor {
        return GetTypedConfigNamesInteractor()
    }

    class AddCustomDropletViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val router: Router,
        private val createCustomServerUseCase: CreateCustomServerInteractor,
        private val getTypedConfigNamesUseCase: GetTypedConfigNamesInteractor
    ): BundleAwareViewModelFactory<AddCustomDropletViewModel>(bundle) {

        override fun getViewModel(): AddCustomDropletViewModel {
            return AddCustomDropletViewModel(
                application,
                router,
                createCustomServerUseCase,
                getTypedConfigNamesUseCase
            )
        }
    }
}