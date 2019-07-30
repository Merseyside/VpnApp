package com.merseyside.dropletapp.presentation.view.fragment.token.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.model.TokenViewModel
import com.merseyside.dropletapp.databinding.FragmentTokenBinding
import com.merseyside.dropletapp.presentation.di.component.DaggerTokenComponent
import com.merseyside.dropletapp.presentation.di.module.TokenModule

class TokenFragment : BaseDropletFragment<FragmentTokenBinding, TokenViewModel>() {

    override fun getTitle(context: Context): String? {
        return getString(R.string.nav_new_token)
    }

    override fun loadingObserver(isLoading: Boolean) {}

    override fun performInjection(bundle: Bundle?) {
        DaggerTokenComponent.builder()
            .appComponent(getAppComponent())
            .tokenModule(getTokenModule(bundle))
            .build().inject(this)
    }

    private fun getTokenModule(bundle: Bundle?): TokenModule {
        return TokenModule(this, bundle)
    }

    override fun setBindingVariable(): Int {
        return BR.viewModel
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_token
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            init()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLayout()
    }

    private fun init() {

    }

    private fun doLayout() {

    }
}