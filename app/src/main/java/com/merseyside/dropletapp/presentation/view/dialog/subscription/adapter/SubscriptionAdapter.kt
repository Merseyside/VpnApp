package com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter

import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.databinding.ViewSubscriptionBinding
import com.merseyside.dropletapp.presentation.view.dialog.subscription.model.SubscriptionItemViewModel
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.merseyLib.adapters.BaseSelectableAdapter
import com.merseyside.merseyLib.utils.ext.getColorFromAttr
import com.merseyside.merseyLib.view.BaseBindingHolder

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
        return R.layout.view_subscription
    }

    override fun onBindViewHolder(holder: BaseBindingHolder<SubscriptionItemViewModel>, position: Int) {
        super.onBindViewHolder(holder, position)

        val binding = holder.binding as ViewSubscriptionBinding
        binding.subscribe.setBackgroundColor(holder.context.getColorFromAttr(R.attr.colorSecondary))
    }
}