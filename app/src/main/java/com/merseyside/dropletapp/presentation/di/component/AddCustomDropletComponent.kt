package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.AddCustomDropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addCustomServer.view.AddCustomDropletFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [AddCustomDropletModule::class])
interface AddCustomDropletComponent {

    fun inject(fragment: AddCustomDropletFragment)
}