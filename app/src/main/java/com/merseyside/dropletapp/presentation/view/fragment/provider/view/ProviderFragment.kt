package com.merseyside.dropletapp.presentation.view.fragment.provider.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.provider.model.ProviderViewModel
import com.merseyside.dropletapp.databinding.FragmentProviderBinding
import com.merseyside.dropletapp.presentation.di.component.DaggerProviderComponent
import com.merseyside.dropletapp.presentation.di.module.ProviderModule
import com.merseyside.dropletapp.presentation.view.activity.main.adapter.ProviderAdapter
import com.merseyside.dropletapp.presentation.view.fragment.provider.adapter.RegionAdapter
import com.merseyside.dropletapp.presentation.view.fragment.provider.adapter.TokenAdapter
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

class ProviderFragment : BaseDropletFragment<FragmentProviderBinding, ProviderViewModel>() {

    private lateinit var providerAdapter: ProviderAdapter
    private lateinit var tokenAdapter: TokenAdapter
    private lateinit var regionAdapter: RegionAdapter

    private val providerObserver = Observer<List<Provider>> {
        providerAdapter = ProviderAdapter(baseActivityView, R.layout.view_provider, it)
        binding.providerSpinner.adapter = providerAdapter
    }

    private val tokenObserver = Observer<List<TokenEntity>> {
        tokenAdapter = TokenAdapter(baseActivityView, R.layout.view_text, it)
        binding.tokenSpinner.adapter = tokenAdapter
    }

    private val regionObserver = Observer<List<RegionPoint>> {
        regionAdapter = RegionAdapter(baseActivityView, R.layout.view_text, it)
        binding.regionSpinner.adapter = regionAdapter
    }

    override fun setBindingVariable(): Int {
        return BR.viewModel
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerProviderComponent.builder()
            .appComponent(getAppComponent())
            .providerModule(getProviderModule(bundle))
            .build().inject(this)
    }

    private fun getProviderModule(bundle: Bundle?): ProviderModule {
        return ProviderModule(this, bundle)
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_provider
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun getTitle(context: Context): String? {
        return null
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
        viewModel.providerLiveData.observe(this, providerObserver)
        viewModel.tokenLiveData.observe(this, tokenObserver)
        viewModel.regionLiveData.observe(this, regionObserver)
    }

    private fun doLayout() {
        binding.providerSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.getTokens(providerAdapter.getItem(position)!!.getId())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        binding.tokenSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.getRegions(tokenAdapter.getItem(position)!!.token)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        binding.regionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setRegion(regionAdapter.getItem(position)!!)
            }

        }

        binding.apply.setOnClickListener {
            viewModel.createServer()
        }
    }

    companion object {

        private const val TAG = "ProviderFragment"

        fun newInstance(): ProviderFragment {
            return ProviderFragment()
        }
    }
}