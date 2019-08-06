package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import com.merseyside.admin.merseylibrary.system.PermissionsManager
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model.AddDropletViewModel
import com.merseyside.dropletapp.databinding.FragmentAddDropletBinding
import com.merseyside.dropletapp.presentation.di.component.DaggerAddDropletComponent
import com.merseyside.dropletapp.presentation.di.module.AddDropletModule
import com.merseyside.dropletapp.presentation.view.activity.main.adapter.ProviderAdapter
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter.RegionAdapter
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter.TokenAdapter
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

class AddDropletFragment : BaseDropletFragment<FragmentAddDropletBinding, AddDropletViewModel>() {

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
        DaggerAddDropletComponent.builder()
            .appComponent(getAppComponent())
            .addDropletModule(getDropletModule(bundle))
            .build().inject(this)
    }

    private fun getDropletModule(bundle: Bundle?): AddDropletModule {
        return AddDropletModule(this, bundle)
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_add_droplet
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
            val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (PermissionsManager.isPermissionsGranted(baseActivityView, permission)) {
                viewModel.createServer()
            } else {
                PermissionsManager.verifyStoragePermissions(this, permission, PERMISSION_ACCESS_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_ACCESS_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.createServer()
                } else {
                    showErrorMsg(getString(R.string.grant_permissions))
                }
            }
        }
    }

    companion object {

        private const val TAG = "ProviderFragment"
        private const val PERMISSION_ACCESS_CODE = 15

        fun newInstance(): AddDropletFragment {
            return AddDropletFragment()
        }
    }
}