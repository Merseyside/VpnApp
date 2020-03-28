package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.AuthModule
import com.merseyside.dropletapp.presentation.view.fragment.auth.view.AuthFragment
import com.merseyside.merseyLib.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [AuthModule::class])
interface AuthComponent {

    fun inject(fragment: AuthFragment)

}