package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.easyAccess.GetVpnConfigInteractor
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model.EasyAccessViewModel
import com.merseyside.dropletapp.utils.PrefsHelper
import com.merseyside.merseyLib.presentation.fragment.BaseFragment
import com.merseyside.merseyLib.presentation.model.BundleAwareViewModelFactory
import com.merseyside.dropletapp.connectionTypes.Builder
import com.merseyside.dropletapp.di.connectionTypeBuilder
import com.merseyside.dropletapp.domain.interactor.GetLockedTypesInteractor
import com.merseyside.dropletapp.domain.interactor.easyAccess.GetRegionsInteractor
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class EasyAccessModule(
    private val fragment: BaseFragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun getEasyAccessViewModelProvider(
        router: Router,
        prefsHelper: PrefsHelper,
        connectionTypeBuilder: Builder,
        getVpnConfigUseCase: GetVpnConfigInteractor,
        getLockedTypesUseCase: GetLockedTypesInteractor,
        getRegionsUseCase: GetRegionsInteractor,
        subscriptionManager: SubscriptionManager
    ): ViewModelProvider.Factory {
        return FreeAccessViewModelProviderFactory(
            bundle,
            router,
            prefsHelper,
            connectionTypeBuilder,
            getVpnConfigUseCase,
            getLockedTypesUseCase,
            getRegionsUseCase,
            subscriptionManager
        )
    }

    @Provides
    internal fun getConnectionTypeBuilder(): Builder {
        return connectionTypeBuilder!!
    }

    @Provides
    internal fun provideGetVpnConfigInteractor(): GetVpnConfigInteractor {
        return GetVpnConfigInteractor()
    }

    @Provides
    internal fun provideGetLockedTypesInteractor(): GetLockedTypesInteractor {
        return GetLockedTypesInteractor()
    }

    @Provides
    internal fun provideGetRegionsInteractor(): GetRegionsInteractor {
        return GetRegionsInteractor()
    }

    @Provides
    internal fun provideDropletViewModel(factory: ViewModelProvider.Factory): EasyAccessViewModel {
        return ViewModelProvider(fragment, factory).get(EasyAccessViewModel::class.java)
    }

    class FreeAccessViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val prefsHelper: PrefsHelper,
        private val connectionTypeBuilder: Builder,
        private val getVpnConfigUseCase: GetVpnConfigInteractor,
        private val getLockedTypesUseCase: GetLockedTypesInteractor,
        private val getRegionsUseCase: GetRegionsInteractor,
        private val subscriptionManager: SubscriptionManager
    ): BundleAwareViewModelFactory<EasyAccessViewModel>(bundle) {

        override fun getViewModel(): EasyAccessViewModel {
            return EasyAccessViewModel(
                router,
                prefsHelper,
                connectionTypeBuilder,
                getVpnConfigUseCase,
                getLockedTypesUseCase,
                getRegionsUseCase,
                subscriptionManager
            )
        }
    }
}