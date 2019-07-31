package com.merseyside.dropletapp.presentation.view.fragment.token.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.service.Service
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.model.TokenViewModel
import com.merseyside.dropletapp.databinding.FragmentTokenBinding
import com.merseyside.dropletapp.presentation.di.component.DaggerTokenComponent
import com.merseyside.dropletapp.presentation.di.module.TokenModule
import com.merseyside.dropletapp.presentation.view.activity.main.adapter.ServiceAdapter

class TokenFragment : BaseDropletFragment<FragmentTokenBinding, TokenViewModel>() {

    private lateinit var serviceAdapter: ServiceAdapter

    private val serviceObserver = Observer<List<Service>> {
        serviceAdapter = ServiceAdapter(baseActivityView, R.layout.view_service, it)
        binding.serviceSpinner.adapter = serviceAdapter
    }

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

        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLayout()
    }

    private fun init() {
        viewModel.serviceLiveData.observe(this, serviceObserver)

    }

    private fun doLayout() {
        binding.save.setOnClickListener {
            val selectedService = serviceAdapter.getItem(binding.serviceSpinner.selectedItemPosition)!!

            viewModel.saveToken(selectedService)
        }

    }

    companion object {

        fun newInstance(): TokenFragment {
            return TokenFragment()
        }
    }
}