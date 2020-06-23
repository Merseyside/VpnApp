package com.merseyside.dropletapp.subscriptions

import android.app.Activity
import android.content.Context
import androidx.annotation.RawRes
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.merseyside.dropletapp.domain.model.SubscriptionInfo
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.merseyside.kmpMerseyLib.utils.time.toTimeUnit
import com.merseyside.merseyLib.utils.Logger
import com.merseyside.merseyLib.utils.billing.BillingManager
import com.merseyside.merseyLib.utils.billing.Subscription
import com.merseyside.merseyLib.utils.exception.NoInternetConnection
import com.merseyside.merseyLib.utils.ext.isNotNullAndEmpty
import com.merseyside.merseyLib.utils.network.isOnline
import com.merseyside.merseyLib.utils.preferences.PreferenceManager
import com.merseyside.merseyLib.utils.serialization.deserialize
import com.merseyside.merseyLib.utils.serialization.serialize

actual class SubscriptionManager {

    class Builder {

        private var context: Context? = null
        private var base64Key: String? = null
        private var credentials: Int? = null

        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun setBase64Key(base64Key: String): Builder {
            this.base64Key = base64Key
            return this
        }

        fun setCredentialsId(@RawRes credentials: Int): Builder {
            this.credentials = credentials
            return this
        }

        fun build(): SubscriptionManager {
            return if (context != null && base64Key != null) {
                SubscriptionManager().apply {
                    context = this@Builder.context!!
                    billingManager = BillingManager(this@Builder.context!!, base64Key!!, credentials!!)
                    preferenceManager = PreferenceManager.Builder(this@Builder.context!!).build()
                }
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    private lateinit var context: Context
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var billingManager: BillingManager

    actual suspend fun isSubscribed(): Boolean {
        val subscription = getSubscriptionInfo()

        if (subscription != null) {
            if (isExpired()) {
                val subscriptionList = billingManager.queryActiveSubscriptionsAsync()

                if (subscriptionList.isNotNullAndEmpty()) {

                    subscriptionList!!.map {
                        if (it is Subscription.ActiveSubscription) {
                            if (subscription.token != it.orderId) {
                                setSubscription(
                                    SubscriptionInfo(
                                        subscriptionId = it.sku,
                                        token = it.orderId,
                                        expiryTime = it.getExpiryTimeMillis().toMillisLong()
                                    )
                                )
                            }

                            return true
                        } else if (subscription.token == it.orderId) {
                            unsubscribe()
                        }
                    }
                } else {
                    Logger.log(this, "here")
                    unsubscribe()
                }

                return getSubscriptionInfo() != null
            } else {
                return true
            }
        }

        return false
    }

    actual fun getSubscriptionInfo(): SubscriptionInfo? {

        return preferenceManager.getNullableString(SUBSCRIPTION)
            ?.deserialize<SubscriptionInfo>()?.let {
                if (it.token.isEmpty()) { null }
                else {
                    it
                }
            }
    }

    actual fun unsubscribe() {
        preferenceManager.put(SUBSCRIPTION, null)
    }

    @Throws(NoInternetConnection::class)
    fun startSubscription(
        activity: Activity,
        skuDetails: SkuDetails,
        onPurchase: (purchase: Purchase) -> Unit,
        onError: (result: BillingResult) -> Unit
    ): BillingResult {
        if (isOnline(context)) {
            billingManager.setOnPurchaseListener(object: BillingManager.OnPurchaseListener {
                override fun onPurchase(purchase: Purchase) {
                    setSubscription(
                        SubscriptionInfo(
                            skuDetails.sku,
                            purchase.purchaseToken,
                            skuDetails.subscriptionPeriod.toTimeUnit().toMillisLong()
                        )
                    )
                    onPurchase.invoke(purchase)
                }

                override fun onError(result: BillingResult) {
                    onError.invoke(result)
                }
            })

            return billingManager.startSubscription(activity, skuDetails)
        } else {
            throw NoInternetConnection()
        }
    }

    private fun isExpired(): Boolean {
        return getSubscriptionInfo()?.let {
            return getCurrentTimeMillis() <= it.expiryTime
        } ?: false
    }

    suspend fun getSubscriptions(skuIds: List<String>): List<VpnSubscription> {
        if (isOnline(context)) {
            val skus = billingManager.getSkuDetails(skuIds)

            if (skus != null) {
//              val activeSubscriptions = getLocalSubscription()

                return skus.map { sku ->
                    //val foundDetails = activeSubscriptions.find { it.sku == sku.sku }

                    VpnSubscription(
                        skuDetails = sku
                    )
                }
            } else throw IllegalStateException("Skus are empty")
        } else throw NoInternetConnection("No internet connection")
    }

    private fun setSubscription(subscriptionInfo: SubscriptionInfo) {
        preferenceManager.put(SUBSCRIPTION, subscriptionInfo.serialize())
    }

    companion object {
        private const val SUBSCRIPTION = "subscription"
    }
}