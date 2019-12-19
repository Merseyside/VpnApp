package com.merseyside.dropletapp.presentation.view.fragment.authFragment.view

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
import com.merseyside.dropletapp.presentation.view.activity.main.view.MainActivity
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.adapter.ProviderAdapter
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.model.AuthViewModel
import com.merseyside.dropletapp.utils.OAuthManager
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseAdapter
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

class AuthFragment : BaseDropletFragment<FragmentAuthBinding, AuthViewModel>(), IAuthFragment {

    private var adapter: ProviderAdapter? = null
    private lateinit var manager: OAuthManager

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

        manager = OAuthManager.Builder(baseActivityView)
            .setOAuthProvider(oAuthConfig)
            .setPostAuthorizationIntent(Intent(activity, MainActivity::class.java).apply { action = postAuthorizationAction })
            .setScopes("read", "write")
            .build()

        manager.startAuthFlow()
    }


    private fun getAuthModule(bundle: Bundle?): AuthModule {
        return AuthModule(this, bundle)
    }

    override fun checkIntent(intent: Intent?) {
        Log.d(TAG, "new intent")

        if (intent != null) {
            when (intent.action) {
                postAuthorizationAction -> if (!intent.hasExtra(
                        USED_INTENT
                    )
                ) {
                    handleAuthorizationResponse(intent)
                    manager.dispose()
                }
                else -> {}
            }
        }
    }

    private fun handleAuthorizationResponse(intent: Intent) {
        Log.d(TAG, "${intent.data}")
        val splits = intent.data.toString().split("access_token=")

        val accessToken = splits[1].split("&")[0]

        viewModel.saveToken(accessToken)
    }

    companion object {
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }

        private const val TAG = "AuthFragment"
        private const val postAuthorizationAction = "com.merseyside.dropletapp.action.AUTH"
        private const val USED_INTENT = "USED_INTENT"
    }
}