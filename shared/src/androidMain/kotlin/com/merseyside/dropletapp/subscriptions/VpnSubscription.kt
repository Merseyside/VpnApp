package com.merseyside.dropletapp.subscriptions

import com.android.billingclient.api.SkuDetails

data class VpnSubscription(
    val skuDetails: SkuDetails,
    val isActive: Boolean = false
)