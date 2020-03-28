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
import com.merseyside.dropletapp.databinding.FragmentAddDropletBinding
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerAddDropletComponent
import com.merseyside.dropletapp.presentation.di.module.AddDropletModule
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter.RegionAdapter
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model.AddDropletViewModel
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.merseyLib.data.serialization.deserialize
import com.merseyside.merseyLib.data.serialization.serialize
import com.merseyside.merseyLib.presentation.view.OnBackPressedListener

class AddDropletFragment : BaseDropletFragment<FragmentAddDropletBinding, AddDropletViewModel>(),
    OnBackPressedListener {

    private lateinit var regionAdapter: RegionAdapter


    private val regionObserver = Observer<List<RegionPoint>> {
        regionAdapter = RegionAdapter(baseActivityView, R.layout.view_text, it)
        binding.regionSpinner.adapter = regionAdapter
    }

    override fun getBindingVariable(): Int {
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

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_droplet
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.nav_add_server)
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
        viewModel.regionLiveData.observe(this, regionObserver)
    }

    private fun doLayout() {

        binding.regionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setRegion(regionAdapter.getItem(position)!!)
            }
        }

        binding.apply.setOnClickListener {
            val permission = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (PermissionsManager.isPermissionsGranted(baseActivityView, permission)) {
                viewModel.createServer()
            } else {
                PermissionsManager.verifyStoragePermissions(this, permission, PERMISSION_ACCESS_CODE)
            }

            closeKeyboard()
        }

        if (arguments?.containsKey(PROVIDER_KEY) == true) {
            viewModel.setProvider(arguments!!.getString(PROVIDER_KEY)!!.deserialize())
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

        private const val PROVIDER_KEY = "provider"

        fun newInstance(provider: Provider): AddDropletFragment {
            val bundle = Bundle().apply {
                putString(PROVIDER_KEY, provider.serialize())
            }

            return AddDropletFragment().also {
                it.arguments = bundle
                it.setRequestCode(23)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }
}