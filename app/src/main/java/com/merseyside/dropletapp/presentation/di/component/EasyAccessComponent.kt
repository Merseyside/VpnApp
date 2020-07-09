package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.EasyAccessModule
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.view.EasyAccessFragment
import com.merseyside.archy.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [EasyAccessModule::class])
interface EasyAccessComponent {

    fun inject(fragment: EasyAccessFragment)
}