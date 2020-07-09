package com.merseyside.dropletapp.presentation.view.fragment.auth.model

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.presentation.view.fragment.auth.adapter.ProviderAdapter
import com.merseyside.adapters.base.UpdateRequest

@BindingAdapter("bind:providers")
fun setOAuthProviders(recyclerView: RecyclerView, providers: List<OAuthProvider>?) {
    if (providers != null) {
        val adapter = recyclerView.adapter?.let { it as ProviderAdapter } ?: ProviderAdapter().also { recyclerView.adapter = it }

        if (adapter.isEmpty()) {
            adapter.add(providers)
        } else {
            adapter.update(UpdateRequest(providers))
        }
    }
}