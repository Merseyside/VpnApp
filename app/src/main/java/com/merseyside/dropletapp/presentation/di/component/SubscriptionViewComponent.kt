package com.merseyside.dropletapp.presentation.di.component

import com.merseyside.dropletapp.presentation.di.module.SubscriptionViewModule
import com.merseyside.dropletapp.presentation.view.dialog.subscription.view.SubscriptionDialog
import com.merseyside.merseyLib.presentation.di.qualifiers.DialogScope
import dagger.Component

@DialogScope
@Component(dependencies = [AppComponent::class], modules = [SubscriptionViewModule::class])
interface SubscriptionViewComponent {

    fun inject(dialog: SubscriptionDialog)
}