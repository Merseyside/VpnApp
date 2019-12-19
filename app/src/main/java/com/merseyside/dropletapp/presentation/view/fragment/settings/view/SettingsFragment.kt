package com.merseyside.dropletapp.presentation.view.fragment.settings.view

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseAdapter
import com.merseyside.mvvmcleanarch.utils.Logger
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseDropletFragment<FragmentSettingsBinding, SettingsViewModel>() {

    private val adapter = TokenAdapter()

    private val tokenObserver = Observer<List<TokenEntity>> {
        if (!adapter.isEmpty()) {
            adapter.removeAll()
        }

        adapter.add(it)
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

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

    private fun doLayout() {
        binding.tokenList.adapter = adapter

        language.currentEntryValue = getLanguage()
        language.setOnValueChangeListener {
            Logger.log(this, "here")
            setLanguage(it)
        }

        viewModel.getAllTokens()
    }

    override fun updateLanguage(context: Context) {
        super.updateLanguage(context)
        language.updateLanguage(context)
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