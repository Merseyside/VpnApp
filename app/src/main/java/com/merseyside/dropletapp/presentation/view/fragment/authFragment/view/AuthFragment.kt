package com.merseyside.dropletapp.presentation.view.fragment.authFragment.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.FragmentAuthBinding
import com.merseyside.dropletapp.domain.model.OAuthConfig
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.presentation.base.BaseDropletFragment
import com.merseyside.dropletapp.presentation.di.component.DaggerAuthComponent
import com.merseyside.dropletapp.presentation.di.module.AuthModule
import com.merseyside.dropletapp.presentation.view.activity.browser.BrowserActivity
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.adapter.ProviderAdapter
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.model.AuthViewModel
import com.merseyside.dropletapp.utils.OAuthBehaviour
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseAdapter
import com.merseyside.mvvmcleanarch.utils.Logger

class AuthFragment : BaseDropletFragment<FragmentAuthBinding, AuthViewModel>(){

    private var adapter: ProviderAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_auth
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.choose_provider)
    }

    override fun loadingObserver(isLoading: Boolean) {}

    override fun performInjection(bundle: Bundle?) {
        DaggerAuthComponent.builder()
            .appComponent(getAppComponent())
            .authModule(getAuthModule(bundle))
            .build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doLayout()
    }

    private fun doLayout() {
        binding.providerList.adapter?.apply {
            adapter = binding.providerList.adapter as ProviderAdapter
        } ?: ProviderAdapter().also {
            adapter = it
            binding.providerList.adapter = adapter
        }


        adapter!!.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<OAuthProvider> {
            override fun onItemClicked(obj: OAuthProvider) {
                if (obj.oAuthConfig != null) {
                    viewModel.setProvider(obj)
                    if (obj.token.isNullOrEmpty()) {
                        startAuthFlow(obj.oAuthConfig!!)
                    } else {
                        viewModel.navigateToServerCreationScreen(obj.provider)
                    }
                }
            }
        })

        viewModel.getOAuthProviders()
    }

    private fun startAuthFlow(oAuthConfig: OAuthConfig) {
        Log.d(TAG, "$oAuthConfig")

        val manager = OAuthBehaviour.Builder(baseActivityView, this, REQUEST_CODE).apply {
            setOAuthConfig(oAuthConfig)

        }.build()

        manager.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val token = data?.extras?.getString(BrowserActivity.TOKEN_KEY, "") ?: ""

                Logger.log(this, token)

                if (token.isNotEmpty()) {
                    viewModel.saveToken(token)
                }
            } else if (resultCode == 2) {
                showErrorMsg(getString(R.string.provider_unavailable))
            }
        }
    }


    private fun getAuthModule(bundle: Bundle?): AuthModule {
        return AuthModule(this, bundle)
    }

    companion object {
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }

        private const val REQUEST_CODE = 1253

        private const val TAG = "AuthFragment"
    }
}