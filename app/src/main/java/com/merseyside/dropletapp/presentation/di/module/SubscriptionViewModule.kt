package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.subscription.GetSubscriptionsInteractor
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionViewModel
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class SubscriptionViewModule(
    private val fragment: Fragment,
    private val bundle: Bundle?
) {

    @Provides
    internal fun provideGetSubscriptionsInteractor(): GetSubscriptionsInteractor {
        return GetSubscriptionsInteractor()
    }

    @Provides
    internal fun provideSubscriptionViewModelFactory(
        application: Application,
        subscriptionManager: SubscriptionManager,
        getSubscriptionsUseCase: GetSubscriptionsInteractor
    ): ViewModelProvider.Factory {
        return DropletViewModelProviderFactory(
            bundle,
            application,
            subscriptionManager,
            getSubscriptionsUseCase
        )
    }

    @Provides
    internal fun provideSubscriptionViewModel(factory: ViewModelProvider.Factory): SubscriptionViewModel {
        return ViewModelProvider(fragment, factory).get(SubscriptionViewModel::class.java)
    }

    class DropletViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val subscriptionManager: SubscriptionManager,
        private val getSubscriptionsUseCase: GetSubscriptionsInteractor
    ): BundleAwareViewModelFactory<SubscriptionViewModel>(bundle) {

        override fun getViewModel(): SubscriptionViewModel {
            return SubscriptionViewModel(
                application,
                subscriptionManager,
                getSubscriptionsUseCase
            )
        }
    }
}