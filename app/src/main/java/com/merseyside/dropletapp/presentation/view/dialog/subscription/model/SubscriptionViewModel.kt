package com.merseyside.dropletapp.presentation.view.dialog.subscription.model

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.merseyside.dropletapp.domain.interactor.subscription.GetSubscriptionsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.subscriptions.VpnSubscription
import com.merseyside.utils.Logger
import com.merseyside.utils.mvvm.SingleEventObservableField
import com.merseyside.utils.mvvm.SingleLiveEvent
import com.merseyside.utils.ext.onChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SubscriptionViewModel(
    private val subscriptionManager: SubscriptionManager,
    private val getSubscriptionsUseCase: GetSubscriptionsInteractor
) : BaseDropletViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val skusList = ObservableField<List<VpnSubscription>>()
    val clickedSubscription = SingleEventObservableField<VpnSubscription?>().apply {
        onChange { _, value, isInitial ->
            if (!isInitial && value != null) {
                clickedSubscriptionEvent.value = value
            }
        }
    }

    private val onPurchaseLiveEvent = SingleLiveEvent<Any>()
    fun getOnPurchaseEvent(): LiveData<Any> = onPurchaseLiveEvent

    val clickedSubscriptionEvent = SingleLiveEvent<VpnSubscription?>()

    init {
        getEnableSkus()
    }

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getSubscriptionsUseCase.cancel()
    }

    private fun getEnableSkus() {
        getSubscriptionsUseCase.execute(
            onComplete = { skus ->
                launch {
                    val subscriptions = subscriptionManager.getSubscriptions(skus.map { it.subscriptionId })

                    skusList.set(subscriptions)
                    Logger.log(this, skus)
                }
            }, onError = {
                launch {
                    val subscriptions = subscriptionManager.getSubscriptions(listOf("test_sub_1"))

                    skusList.set(subscriptions)
                }
            }, onPreExecute = {
                showProgress()
            }, onPostExecute = {
                hideProgress()
            }
        )
    }
}