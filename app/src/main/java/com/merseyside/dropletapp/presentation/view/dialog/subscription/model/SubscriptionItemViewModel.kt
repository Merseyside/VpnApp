package com.merseyside.dropletapp.presentation.view.dialog.subscription.model

import android.content.Context
import androidx.databinding.Bindable
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter.SubscriptionAdapter
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.adapters.model.BaseSelectableAdapterViewModel
import com.merseyside.archy.presentation.interfaces.IStringHelper
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.utils.application
import com.merseyside.utils.billing.subscriptionPeriod
import com.merseyside.utils.ext.trimTrailingZero

class SubscriptionItemViewModel(
    obj: VpnSubscription,
    private val subscribeListener: SubscriptionAdapter.SubscribeListener?
) : BaseSelectableAdapterViewModel<VpnSubscription>(obj), IStringHelper {

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
    fun getPeriodAndPrice(): String {
        return getString(R.string.price_with_period, getPrice(), getPeriod())
    }

    @Bindable
    fun isExpanded(): Boolean {
        return isSelected()
    }

    @Bindable
    fun getPricePerMonth(): String {
        return "$42"
    }

    @Bindable
    fun getPrice(): String {
        val price = (obj.skuDetails.priceAmountMicros / 1_000_000F).trimTrailingZero()

        return "${formatPrice(price)} ${obj.skuDetails.priceCurrencyCode}"
    }

    private fun formatPrice(price: String): String {
        val splits = price.split(".")

        return if (splits.size == 2 && splits[1].length > 2 ) {
            splits[0] + "." + splits[1].take(2)
        } else {
            price
        }
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

    override fun getLocaleContext(): Context {
        return application
    }
}