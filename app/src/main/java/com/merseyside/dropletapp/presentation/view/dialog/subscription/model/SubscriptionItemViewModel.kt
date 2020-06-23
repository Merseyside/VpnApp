package com.merseyside.dropletapp.presentation.view.dialog.subscription.model

import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter.SubscriptionAdapter
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.merseyLib.model.BaseSelectableAdapterViewModel
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.utils.billing.subscriptionPeriod
import com.merseyside.merseyLib.utils.ext.trimTrailingZero

class SubscriptionItemViewModel(
    override var obj: VpnSubscription,
    private val subscribeListener: SubscriptionAdapter.SubscribeListener?
) : BaseSelectableAdapterViewModel<VpnSubscription>(obj) {

    init {
        if (obj.isActive) {
            Logger.log(this, obj)
        }
    }

    override fun areItemsTheSame(obj: VpnSubscription): Boolean {
        return this.obj == obj
    }

    @Bindable
    fun getTitle(): String {
        obj.skuDetails.title.let {
            return if (it.contains("(MyVPN.RUN)")) {
                it.replace("(MyVPN.RUN)", "")
            } else {
                it
            }
        }
    }

    @Bindable
    fun getDescription(): String {
        return obj.skuDetails.description.replace("\n", "")
    }

    @Bindable
    fun getPeriod(): String {
        return obj.skuDetails.subscriptionPeriod().getHumanReadablePeriod()
    }

    @Bindable
    fun isExpanded(): Boolean {
        return isSelected()
    }

    @Bindable
    fun getPrice(): String {
        return "Subscribe for ${(obj.skuDetails.priceAmountMicros / 1_000_000F).trimTrailingZero()} ${obj.skuDetails!!.priceCurrencyCode}"
    }

    @Bindable
    fun isActive(): Boolean {
        return obj.isActive
    }

    fun onSubscribeClick() {
        subscribeListener?.onSubscribeClick(obj)
    }

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.expanded)
    }

    override fun areContentsTheSame(obj: VpnSubscription): Boolean {
        return false
    }

    override fun compareTo(obj: VpnSubscription): Int {
        return 0
    }

    override fun onSelectedChanged(isSelected: Boolean) {
        notifyUpdate()
    }

    override fun notifySelectEnabled(isEnabled: Boolean) {}
}