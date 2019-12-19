package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.TokenModule
import com.merseyside.dropletapp.presentation.view.fragment.token.view.TokenFragment
import com.merseyside.mvvmcleanarch.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [TokenModule::class])
interface TokenComponent {

    fun inject(fragment: TokenFragment)
}