package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.AddDropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view.AddDropletFragment
import com.merseyside.archy.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [AddDropletModule::class])
interface AddDropletComponent {

    fun inject(fragment: AddDropletFragment)
}