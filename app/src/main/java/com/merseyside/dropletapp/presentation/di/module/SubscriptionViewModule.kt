package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.subscription.GetSubscriptionsInteractor
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionViewModel
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.archy.model.BundleAwareViewModelFactory
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
        subscriptionManager: SubscriptionManager,
        getSubscriptionsUseCase: GetSubscriptionsInteractor
    ): ViewModelProvider.Factory {
        return DropletViewModelProviderFactory(
            bundle,
            subscriptionManager,
            getSubscriptionsUseCase
        )
    }

    @Provides
    internal fun provideSubscriptionViewModel(factory: ViewModelProvider.Factory): SubscriptionViewModel {
        return ViewModelProviders.of(fragment, factory).get(SubscriptionViewModel::class.java)
    }

    class DropletViewModelProviderFactory(
        bundle: Bundle?,
        private val subscriptionManager: SubscriptionManager,
        private val getSubscriptionsUseCase: GetSubscriptionsInteractor
    ): BundleAwareViewModelFactory<SubscriptionViewModel>(bundle) {

        override fun getViewModel(): SubscriptionViewModel {
            return SubscriptionViewModel(
                subscriptionManager,
                getSubscriptionsUseCase
            )
        }
    }
}