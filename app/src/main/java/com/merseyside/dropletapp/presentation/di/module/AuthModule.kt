package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.presentation.view.activity.auth.model.MainViewModel
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class AuthModule(
    private val activity: BaseActivity,
    private val bundle: Bundle?
) {

    @Provides
    internal fun addAuthActivityViewModelProvider(
        router: Router
    ): ViewModelProvider.Factory {
        return AddAuthActivityViewModelProviderFactory(bundle, router)
    }

    @Provides
    internal fun provideAuthActivityViewModel(factory: ViewModelProvider.Factory): MainViewModel {
        return ViewModelProviders.of(activity, factory).get(MainViewModel::class.java)
    }

    class AddAuthActivityViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router
    ): BundleAwareViewModelFactory<MainViewModel>(bundle) {

        override fun getViewModel(): MainViewModel {
            return MainViewModel(router)
        }
    }
}