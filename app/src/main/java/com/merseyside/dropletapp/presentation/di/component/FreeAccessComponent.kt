package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.FreeAccessModule
import com.merseyside.dropletapp.presentation.view.fragment.free.view.FreeAccessFragment
import com.merseyside.merseyLib.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [FreeAccessModule::class])
interface FreeAccessComponent {

    fun inject(fragment: FreeAccessFragment)
}