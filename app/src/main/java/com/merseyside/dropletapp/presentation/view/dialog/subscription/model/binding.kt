package com.merseyside.dropletapp.presentation.view.dialog.subscription.model

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.dropletapp.presentation.view.dialog.subscription.adapter.SubscriptionAdapter
import com.merseyside.dropletapp.subscriptions.VpnSubscription

private fun getSkuAdapter(): SubscriptionAdapter {
    return SubscriptionAdapter()
}

private var clickedItem: VpnSubscription? = null

@BindingAdapter("app:skus")
fun setSkus(recyclerView: RecyclerView, skus: List<VpnSubscription>?) {

    if (skus != null) {
        var adapter = recyclerView.adapter ?: getSkuAdapter().also {
            recyclerView.adapter = it
        }

        adapter = adapter as SubscriptionAdapter

        if (adapter.isNotEmpty()) {
            adapter.clear()
        }

        adapter.add(skus)
    }
}

@BindingAdapter(value = ["bind:subscribeClickAttrChanged"]) // AttrChanged required postfix
fun clickSubsciptionListener(recyclerView: RecyclerView, listener: InverseBindingListener?) {
    var adapter = recyclerView.adapter ?: getSkuAdapter().also {
        recyclerView.adapter = it
    }

    adapter = adapter as SubscriptionAdapter

    adapter.setSubscribeListener(object: SubscriptionAdapter.SubscribeListener {
        override fun onSubscribeClick(sub: VpnSubscription) {
            clickedItem = sub

            listener?.onChange()
        }

    })
}

@BindingAdapter("bind:subscribeClick")
fun setClickSubsciption(recyclerView: RecyclerView, sub: VpnSubscription?) {}

@InverseBindingAdapter(attribute = "bind:subscribeClick")
fun getWord(recyclerView: RecyclerView): VpnSubscription? {
    return clickedItem
}