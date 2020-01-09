package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.AuthModule
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.view.AuthFragment
import com.merseyside.mvvmcleanarch.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [AuthModule::class])
interface AuthComponent {

    fun inject(fragment: AuthFragment)

}