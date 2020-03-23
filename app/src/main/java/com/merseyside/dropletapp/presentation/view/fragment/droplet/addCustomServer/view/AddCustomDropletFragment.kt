package com.merseyside.dropletapp.presentation.view.fragment.droplet.addCustomServer.view

import android.content.Context
import android.os.Bundle
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentCustomDropletBinding
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerAddCustomDropletComponent
import com.merseyside.dropletapp.presentation.di.module.AddCustomDropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addCustomServer.model.AddCustomDropletViewModel

class AddCustomDropletFragment
    : BaseDropletFragment<FragmentCustomDropletBinding, AddCustomDropletViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerAddCustomDropletComponent.builder()
            .appComponent(getAppComponent())
            .addCustomDropletModule(getAddCustomDropletModule(bundle))
            .build().inject(this)
    }

    private fun getAddCustomDropletModule(bundle: Bundle?): AddCustomDropletModule {
        return AddCustomDropletModule(this, bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_custom_droplet
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.custom_server)
    }

    companion object {
        fun newInstance(): AddCustomDropletFragment {
            return AddCustomDropletFragment()
        }
    }
}