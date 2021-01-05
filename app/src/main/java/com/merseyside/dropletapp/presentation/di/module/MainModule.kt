package com.merseyside.dropletapp.presentation.di.module

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.view.activity.main.model.MainViewModel
import com.merseyside.archy.presentation.activity.BaseActivity
import com.merseyside.archy.presentation.model.BundleAwareViewModelFactory
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
        application: Application,
        router: Router,
        getDropletsUseCase: GetDropletsInteractor
    ): ViewModelProvider.Factory {
        return MainActivityViewModelProviderFactory(bundle, application, router, getDropletsUseCase)
    }

    @Provides
    internal fun getDropletsInteractor(): GetDropletsInteractor {
        return GetDropletsInteractor()
    }

    @Provides
    internal fun provideMainActivityViewModel(factory: ViewModelProvider.Factory): MainViewModel {
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    class MainActivityViewModelProviderFactory(
        bundle: Bundle?,
        private val application: Application,
        private val router: Router,
        private val getDropletsUseCase: GetDropletsInteractor
    ): BundleAwareViewModelFactory<MainViewModel>(bundle) {

        override fun getViewModel(): MainViewModel {
            return MainViewModel(application, router, getDropletsUseCase)
        }
    }
}