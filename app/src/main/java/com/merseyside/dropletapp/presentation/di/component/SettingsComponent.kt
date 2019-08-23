package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.SettingsModule
import com.merseyside.dropletapp.presentation.view.fragment.settings.view.SettingsFragment
import com.upstream.basemvvmimpl.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [SettingsModule::class])
interface SettingsComponent {

    fun inject(fragment: SettingsFragment)
}