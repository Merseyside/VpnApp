package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.presentation.view.activity.main.model.MainViewModel
import com.upstream.basemvvmimpl.presentation.activity.BaseActivity
import com.upstream.basemvvmimpl.presentation.model.BundleAwareViewModelFactory
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Router

@Module
class MainModule(
    private val activity: BaseActivity,
    private val bundle: Bundle?
) {

    @Provides
    internal fun addMainActivityViewModelProvider(
        router: Router
    ): ViewModelProvider.Factory {
        return MainActivityViewModelProviderFactory(bundle, router)
    }

    @Provides
    internal fun provideMainActivityViewModel(factory: ViewModelProvider.Factory): MainViewModel {
        return ViewModelProviders.of(activity, factory).get(MainViewModel::class.java)
    }

    class MainActivityViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router
    ): BundleAwareViewModelFactory<MainViewModel>(bundle) {

        override fun getViewModel(): MainViewModel {
            return MainViewModel(router)
        }
    }
}