package com.merseyside.dropletapp.presentation.view.fragment.settings.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.databinding.FragmentSettingsBinding
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerSettingsComponent
import com.merseyside.dropletapp.presentation.di.module.SettingsModule
import com.merseyside.dropletapp.presentation.view.fragment.settings.adapter.TokenAdapter
import com.merseyside.dropletapp.presentation.view.fragment.settings.model.SettingsViewModel
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.utils.LogoutBehavior
import com.merseyside.adapters.base.BaseAdapter
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseDropletFragment<FragmentSettingsBinding, SettingsViewModel>() {

    private val adapter = TokenAdapter()

    private val tokenObserver = Observer<List<TokenEntity>> {
        if (!adapter.isEmpty()) {
            adapter.clear()
        }

        adapter.add(it)
    }

    override fun loadingObserver(isLoading: Boolean) {}

    override fun performInjection(bundle: Bundle?) {
        DaggerSettingsComponent.builder()
            .appComponent(getAppComponent())
            .settingsModule(SettingsModule(this, bundle))
            .build().inject(this)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_settings
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
        adapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener<TokenEntity> {
            override fun onItemClicked(obj: TokenEntity) {
                viewModel.deleteToken(obj)
            }
        })

        viewModel.tokenLiveData.observe(this, tokenObserver)
    }

    private fun logout(token: TokenEntity) {
        val provider = Provider.getProviderById(providerId = token.providerId)

        val uri = when(provider) {
            is Provider.DigitalOcean -> "https://cloud.digitalocean.com/logout"
            is Provider.CryptoServers -> "https://cryptoservers.net/logout"
            is Provider.Linode -> "https://cloud.linode.com/logout"
            else -> ""
        }

        LogoutBehavior(baseActivity, uri).start()
    }

    private fun doLayout() {
        binding.tokenList.adapter = adapter

        language.currentEntryValue = getLanguage()
        language.setOnValueChangeListener {
            setLanguage(it)
        }

        updateLanguage(context)

        viewModel.getAllTokens()
    }

    override fun updateLanguage(context: Context) {
        super.updateLanguage(context)
        language.updateLanguage(context)

        adapter.notifyUpdateAll()
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.nav_settings)
    }

    companion object {

        private const val TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}