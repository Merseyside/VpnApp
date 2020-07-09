package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.DropletListModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view.DropletListFragment
import com.merseyside.archy.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [DropletListModule::class])
interface DropletListComponent {

    fun inject(fragment: DropletListFragment)
}