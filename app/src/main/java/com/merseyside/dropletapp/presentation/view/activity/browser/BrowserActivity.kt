package com.merseyside.dropletapp.presentation.view.activity.browser

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.view.MyWebViewClient
import com.merseyside.merseyLib.presentation.activity.BaseActivity
import com.merseyside.merseyLib.utils.Logger


class BrowserActivity : BaseActivity() {

    private lateinit var client: MyWebViewClient
    private lateinit var webView: WebView

    override fun getLayoutId(): Int {
        return R.layout.activity_browser
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    override fun getFragmentContainer(): Int? {
        return null
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        if (Build.VERSION.SDK_INT in 21..24) {
            overrideConfiguration.uiMode =
                overrideConfiguration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = findViewById(R.id.webView)

        client = MyWebViewClient()
        webView.webViewClient = client
        //webView.webChromeClient = WebChromeClient()

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.defaultTextEncodingName = "utf-8"


        if (intent.extras?.containsKey(HOST_KEY) == true) {
            client.setHost(intent.extras!!.getString(HOST_KEY)!!)
            client.apply {
                setOnAccessTokenCallback(object: MyWebViewClient.OnAccessTokenCallback {
                    override fun onToken(token: String) {
                        Logger.log(this@BrowserActivity, token)
                        val intent = Intent().apply {
                            putExtra(TOKEN_KEY, token)
                        }

                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                    override fun onError(errorCode: Int) {
                        val intent = Intent().apply {
                            putExtra(ERROR_KEY, errorCode)
                        }

                        setResult(PROVIDER_UNAVAILABLE, intent)
                        finish()
                    }

                })
            }
        }

        val uri = intent.extras!!.getString(URI_KEY)
        webView.loadUrl(uri).also { Logger.log(this, uri!!) }
    }

    override fun onStop() {
        super.onStop()

        webView.clearCache(true)
        webView.clearHistory()
        clearCookie()
    }

    override fun performInjection(bundle: Bundle?) {}

    private fun clearCookie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieSyncMngr: CookieSyncManager = CookieSyncManager.createInstance(this)
            cookieSyncMngr.startSync()
            val cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }

    }

    companion object {
        private const val HOST_KEY = "host"
        private const val URI_KEY = "uri"

        const val PROVIDER_UNAVAILABLE = 2

        const val TOKEN_KEY = "token"
        const val ERROR_KEY = "error"

        fun getStartAuthFlowIntent(activity: Activity, host: String, uri: String): Intent {

            if (host.isNotEmpty() && uri.isNotEmpty()) {
                return Intent(activity, BrowserActivity::class.java).apply {
                    putExtra(HOST_KEY, host)
                    putExtra(URI_KEY, uri)
                }
            }

            throw IllegalArgumentException()
        }

        fun getLogoutFlowIntent(activity: Activity, uri: String): Intent {
            if (uri.isNotEmpty()) {
                return Intent(activity, BrowserActivity::class.java).apply {
                    putExtra(URI_KEY, uri)
                }
            }

            throw IllegalArgumentException()
        }
     }

}