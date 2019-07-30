package com.merseyside.dropletapp.presentation.di.component

import android.app.Application
import android.content.Context
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.presentation.di.module.AppModule
import com.merseyside.dropletapp.presentation.di.module.NavigationModule
import com.upstream.basemvvmimpl.presentation.di.qualifiers.ApplicationContext
import com.upstream.basemvvmimpl.presentation.utils.PreferenceManager
import dagger.Component
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NavigationModule::class])
interface AppComponent {

    fun inject(application: VpnApplication)

    @ApplicationContext
    fun context() : Context

    fun application() : Application

    fun getPreferenceManager(): PreferenceManager

    fun navigation() : NavigatorHolder

    fun router() : Router
}