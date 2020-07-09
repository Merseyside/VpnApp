package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.MainModule
import com.merseyside.dropletapp.presentation.view.activity.main.view.MainActivity
import com.merseyside.archy.di.qualifiers.ActivityScope
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [MainModule::class])
interface MainComponent {

    fun inject(activity: MainActivity)
}