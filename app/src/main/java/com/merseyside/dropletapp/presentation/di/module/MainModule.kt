package com.merseyside.dropletapp.presentation.di.module

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.view.activity.main.model.MainViewModel
import com.merseyside.archy.presentation.activity.BaseActivity
import com.merseyside.archy.model.BundleAwareViewModelFactory
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
        router: Router,
        getDropletsUseCase: GetDropletsInteractor
    ): ViewModelProvider.Factory {
        return MainActivityViewModelProviderFactory(bundle, router, getDropletsUseCase)
    }

    @Provides
    internal fun getDropletsInteractor(): GetDropletsInteractor {
        return GetDropletsInteractor()
    }

    @Provides
    internal fun provideMainActivityViewModel(factory: ViewModelProvider.Factory): MainViewModel {
        return ViewModelProviders.of(activity, factory).get(MainViewModel::class.java)
    }

    class MainActivityViewModelProviderFactory(
        bundle: Bundle?,
        private val router: Router,
        private val getDropletsUseCase: GetDropletsInteractor
    ): BundleAwareViewModelFactory<MainViewModel>(bundle) {

        override fun getViewModel(): MainViewModel {
            return MainViewModel(router, getDropletsUseCase)
        }
    }
}