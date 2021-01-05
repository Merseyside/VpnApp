package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.DeleteTokenInteractor
import com.merseyside.dropletapp.domain.interactor.GetAllTokensInteractor
import com.merseyside.dropletapp.presentation.view.fragment.settings.model.SettingsViewModel
import com.merseyside.archy.presentation.fragment.BaseFragment
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class SettingsModule(private val fragment: BaseFragment,
                     private val bundle: Bundle?) {

    @Provides
    internal fun provideFetAllTokensInteractor(): GetAllTokensInteractor {
        return GetAllTokensInteractor()
    }

    @Provides
    internal fun provideDeleteTokenInteractor() : DeleteTokenInteractor {
        return DeleteTokenInteractor()
    }

    @Provides
    internal fun provideSettingsFragmentViewModelProvider(
        application: Application,
        router: Router,
        getAllTokensUseCase: GetAllTokensInteractor,
        deleteTokenUseCase: DeleteTokenInteractor
    ): ViewModelProvider.Factory {
        return SettingsFragmentViewModelProviderFactory(bundle, application, router, getAllTokensUseCase, deleteTokenUseCase)
    }

    @Provides
    internal fun provideSettingsFragmentViewModel(factory: ViewModelProvider.Factory): SettingsViewModel {
        return ViewModelProvider(fragment, factory).get(SettingsViewModel::class.java)
    }

    class SettingsFragmentViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val router: Router,
        private val getAllTokensUseCase: GetAllTokensInteractor,
        private val deleteTokenUseCase: DeleteTokenInteractor
    ): BundleAwareViewModelFactory<SettingsViewModel>(bundle) {

        override fun getViewModel(): SettingsViewModel {
            return SettingsViewModel(application, router, getAllTokensUseCase, deleteTokenUseCase)
        }
    }
}