package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.SettingsModule
import com.merseyside.dropletapp.presentation.view.fragment.settings.view.SettingsFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [SettingsModule::class])
interface SettingsComponent {

    fun inject(fragment: SettingsFragment)
}