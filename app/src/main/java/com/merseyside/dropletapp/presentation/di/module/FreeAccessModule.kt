package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.GetVpnConfigInteractor
import com.merseyside.dropletapp.presentation.view.fragment.free.model.FreeAccessViewModel
import com.merseyside.dropletapp.utils.PrefsHelper
import com.merseyside.merseyLib.presentation.fragment.BaseFragment
import com.merseyside.merseyLib.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class FreeAccessModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun addDropletViewModelProvider(
        router: Router,
        prefsHelper: PrefsHelper,
        getVpnConfigUseCase: GetVpnConfigInteractor
    ): ViewModelProvider.Factory {
        return FreeAccessViewModelProviderFactory(
            bundle,
            router,
            prefsHelper,
            getVpnConfigUseCase
        )
    }

    @Provides
    internal fun provideGetVpnConfigInteractor(): GetVpnConfigInteractor {
        return GetVpnConfigInteractor()
    }

    @Provides
    internal fun provideDropletViewModel(factory: ViewModelProvider.Factory): FreeAccessViewModel {
        return ViewModelProvider(fragment, factory).get(FreeAccessViewModel::class.java)
    }

    class FreeAccessViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val prefsHelper: PrefsHelper,
        private val getVpnConfigUseCase: GetVpnConfigInteractor
    ): BundleAwareViewModelFactory<FreeAccessViewModel>(bundle) {

        override fun getViewModel(): FreeAccessViewModel {
            return FreeAccessViewModel(router, prefsHelper, getVpnConfigUseCase)
        }
    }
}