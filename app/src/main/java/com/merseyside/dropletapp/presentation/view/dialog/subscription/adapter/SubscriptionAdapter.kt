package com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter

import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.databinding.ViewSubscriptionBinding
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionItemViewModel
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.adapters.base.BaseSelectableAdapter
import com.merseyside.adapters.view.BaseBindingHolder
import com.merseyside.dropletapp.databinding.ViewSubscription1Binding
import com.merseyside.utils.ext.getColorFromAttr

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

    override fun onBindViewHolder(holder: BaseBindingHolder<SubscriptionItemViewModel>, position: Int) {
        super.onBindViewHolder(holder, position)

        val binding = holder.binding as ViewSubscription1Binding
        //binding.subscribe.setBackgroundColor(holder.context.getColorFromAttr(R.attr.colorSecondary))
    }
}