package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.archy.di.qualifiers.DialogScope
import com.merseyside.dropletapp.presentation.di.module.SubscriptionViewModule
import com.merseyside.dropletapp.presentation.view.dialog.subscription.view.SubscriptionDialog
import dagger.Component

@DialogScope
@Component(dependencies = [AppComponent::class], modules = [SubscriptionViewModule::class])
interface SubscriptionViewComponent {

    fun inject(dialog: SubscriptionDialog)
}