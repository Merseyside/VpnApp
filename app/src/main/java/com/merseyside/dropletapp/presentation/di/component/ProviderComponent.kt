package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.ProviderModule
import com.merseyside.dropletapp.presentation.view.fragment.provider.view.ProviderFragment
import com.upstream.basemvvmimpl.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ProviderModule::class])
interface ProviderComponent {

    fun inject(fragment: ProviderFragment)
}