package com.merseyside.dropletapp.presentation.view.view

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.merseyside.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.Request

class MyWebViewClient: WebViewClient() {

    private var host: String? = null

    private var onAccessTokenCallback: OnAccessTokenCallback? = null

    interface OnAccessTokenCallback {
        fun onToken(token: String)

        fun onError(errorCode: Int)
    }

    fun setOnAccessTokenCallback(callback: OnAccessTokenCallback) {
        this.onAccessTokenCallback = callback
    }

    fun setHost(host: String) {
        this.host = host
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

        if (url != null) {

            if (host != null) {
                if (getHost(url).contains(host!!)) {
                    getAccessToken(url)
                } else {
                    //makeRequest(url)
                    view?.loadUrl(url)
                }
            } else {
                //makeRequest(url)
                view?.loadUrl(url)
            }

            return true
        }

        return false
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        Logger.log(this, request!!.url!!)

        //val url = getValidUrl(URLDecoder.decode(request.url!!.toString())).also { Logger.log(this, it) }
        val url = request.url!!.toString()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && request.url != null) {
            if (host != null) {
                if (getHost(request.url).contains(host!!)) {
                    getAccessToken(url)
                } else {
                    //makeRequest(url)
                    view?.loadUrl(url)
                }
            } else {
                //makeRequest(url)
                view?.loadUrl(url)
            }

            true
        } else {
            false
        }
    }

    private fun getAccessToken(url: String) {

        val splits = url.split("access_token=")

        val accessToken = splits[1].split("&")[0].also { Logger.log(this, it) }

        onAccessTokenCallback?.onToken(accessToken)
    }

    private fun getHost(url: String): String {
        val uri = Uri.parse(url)

        return getHost(uri)
    }

    private fun getHost(uri: Uri): String {
        return uri.host.toString().also { Logger.log(TAG, "host = $it") }
    }

//    override fun onReceivedError(
//        view: WebView?,
//        errorCode: Int,
//        description: String?,
//        failingUrl: String?
//    ) {
//        super.onReceivedError(view, errorCode, description, failingUrl)
//
//        onAccessTokenCallback?.onError(errorCode)
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    override fun onReceivedError(
//        view: WebView?,
//        request: WebResourceRequest?,
//        error: WebResourceError?
//    ) {
//        super.onReceivedError(view, request, error)
//
//        if (error != null) {
//            onAccessTokenCallback?.onError(error.errorCode)
//            return
//        }
//
//        onAccessTokenCallback?.onError(404)
//    }

//    private fun makeRequest(url: String) {
//        Thread {
//            val client = OkHttpClient()
//
//            val request: Request = Request.Builder()
//                .url(url)
//                .build()
//
//            try {
//
//                val response = client.newCall(request).execute()
//
//            } catch (e: Exception) {
//                val msg = e.message
//                e.printStackTrace()
//            }
//        }.start()
//    }

//    private fun getValidUrl(url: String): String {
//        return url
//    }


    companion object {
        private const val TAG = "MyWebView"
    }
}