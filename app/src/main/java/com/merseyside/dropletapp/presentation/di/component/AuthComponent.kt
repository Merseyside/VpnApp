package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.AuthModule
import com.merseyside.dropletapp.presentation.view.activity.auth.view.MainActivity
import com.upstream.basemvvmimpl.presentation.di.qualifiers.ActivityScope
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [AuthModule::class])
interface AuthComponent {

    fun inject(activity: MainActivity)
}