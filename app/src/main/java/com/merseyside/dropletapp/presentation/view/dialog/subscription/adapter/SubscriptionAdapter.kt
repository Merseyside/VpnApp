package com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter

import com.merseyside.adapters.base.BaseSelectableAdapter
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionItemViewModel
import com.merseyside.dropletapp.subscriptions.VpnSubscription

class SubscriptionAdapter : BaseSelectableAdapter<VpnSubscription, SubscriptionItemViewModel>(
    isAllowToCancelSelection = true
) {

    interface SubscribeListener {
        fun onSubscribeClick(sub: VpnSubscription)
    }

    private var subscribeListener: SubscribeListener? = null

    fun setSubscribeListener(listener: SubscribeListener) {
        this.subscribeListener = listener
    }

    override fun createItemViewModel(obj: VpnSubscription): SubscriptionItemViewModel {
        return SubscriptionItemViewModel(obj, subscribeListener)
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_subscription1
    }
}