package com.merseyside.dropletapp.utils

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.merseyside.dropletapp.domain.model.OAuthConfig
import com.merseyside.dropletapp.presentation.view.activity.browser.BrowserActivity
import java.lang.StringBuilder

class OAuthBehaviour private constructor() {

    private lateinit var activity: Activity
    private var fragment: Fragment? = null

    private var requestCode: Int? = null

    constructor(
        activity: Activity,
        requestCode: Int
    ) : this() {

        this.activity = activity
        this.requestCode = requestCode
    }

    constructor(
        activity: Activity,
        fragment: Fragment? = null,
        requestCode: Int) : this() {

        this.activity = activity
        this.fragment = fragment
        this.requestCode = requestCode
    }

    private var authEndPoint: String? = null
    private var host: String? = null
    private var clientId: String? = null
    private var redirectUrl: String? = null
    private var scopes: String? = null

    class Builder() {

        private lateinit var oAuthBehaviour: OAuthBehaviour

        constructor(
            activity: Activity,
            requestCode: Int
        ) : this() {

            oAuthBehaviour = OAuthBehaviour(activity, requestCode)
        }

        constructor(
            activity: Activity,
            fragment: Fragment,
            requestCode: Int
        ) : this() {

            oAuthBehaviour = OAuthBehaviour(activity, fragment, requestCode)
        }

        fun setOAuthConfig(oAuthConfig: OAuthConfig): Builder {
            oAuthConfig.let {
                oAuthBehaviour.authEndPoint = it.authEndPoint
                oAuthBehaviour.host = it.host
                oAuthBehaviour.clientId = it.clientId
                oAuthBehaviour.redirectUrl = it.redirectUrl
                oAuthBehaviour.scopes = it.scopes

                return this
            }
        }

        fun setHost(host: String): Builder {
            oAuthBehaviour.host = host
            return this
        }

        fun setScopes(scopes: String): Builder {
            oAuthBehaviour.scopes = scopes
            return this
        }

        fun setAuthPoint(authPoint: String) : Builder {
            oAuthBehaviour.authEndPoint = authPoint
            return this
        }

        fun setClientId(clientId: String) : Builder {
            oAuthBehaviour.clientId = clientId
            return this
        }

        fun setRedirectUri(redirectUri: String): Builder {
            oAuthBehaviour.redirectUrl = redirectUri
            return this
        }

        fun build(): OAuthBehaviour {
            oAuthBehaviour.let {
                if (it.authEndPoint != null &&
                    it.host != null &&
                    it.scopes != null &&
                    it.clientId != null &&
                    it.redirectUrl != null) return it

                throw IllegalArgumentException("Need more arguments")
            }
        }
    }

    fun start() {
        val uri = generateUri()

        if (fragment != null) {
            val intent = BrowserActivity.getStartAuthFlowIntent(activity, host!!, uri)

            fragment!!.startActivityForResult(intent, requestCode!!)
        } else {
            val intent = BrowserActivity.getStartAuthFlowIntent(activity, host!!, uri)

            activity.startActivityForResult(intent, requestCode!!)
        }
    }

    private fun generateUri(): String {
        val builder = StringBuilder("${authEndPoint}?").apply {
            append("$REDIRECT_URI=$redirectUrl&")
            append("$CLIENT_ID=$clientId&")
            append("$RESPONSE_TYPE=token&")
            append("$SCOPE=$scopes")
        }

        return builder.toString().also { Log.d(TAG, it) }
    }

    companion object {
        private const val TAG = "OauthManager"

        private const val REDIRECT_URI = "redirect_uri"
        private const val CLIENT_ID = "client_id"
        private const val RESPONSE_TYPE = "response_type"
        private const val SCOPE = "scope"
    }
}