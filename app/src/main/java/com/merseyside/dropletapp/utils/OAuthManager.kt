package com.merseyside.dropletapp.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.merseyside.dropletapp.domain.model.OAuthConfig
import net.openid.appauth.*
import net.openid.appauth.browser.BrowserWhitelist
import net.openid.appauth.browser.VersionedBrowserMatcher


class OAuthManager private constructor(private val activity: Activity) {

    private var authEndPoint: String? = null
    private var tokenEndPoint: String? = null
    private var clientId: String? = null
    private var redirectUrl: String? = null
    private var postAuthorizationIntent: Intent? = null

    private var scopes: Array<out String>? = arrayOf("")

    val authorizationService: AuthorizationService

    init {

        if (isPackageInstalled("com.android.chrome", activity.packageManager)) {
            val appAuthConfig = AppAuthConfiguration.Builder()
                .setBrowserMatcher(
                    BrowserWhitelist(
                        VersionedBrowserMatcher.CHROME_BROWSER
                    )
                )
                .build()

            authorizationService = AuthorizationService(activity, appAuthConfig)
        } else {
            authorizationService = AuthorizationService(activity)
        }
    }

    class Builder(activity: Activity) {

        private val oAuthManager = OAuthManager(activity)

        fun setOAuthProvider(oAuthProvider: OAuthConfig): Builder {
            oAuthProvider.let {
                oAuthManager.authEndPoint = it.authEndPoint
                oAuthManager.tokenEndPoint = it.tokenEndPoint
                oAuthManager.clientId = it.clientId
                oAuthManager.redirectUrl = it.redirectUrl

                return this
            }
        }

        fun setPostAuthorizationIntent(intent: Intent): Builder {
            oAuthManager.postAuthorizationIntent = intent
            return this
        }

        fun setScopes(vararg scopes: String): Builder {
            oAuthManager.scopes = scopes
            return this
        }

        fun setAuthPoint(authPoint: String) : Builder {
            oAuthManager.authEndPoint = authPoint
            return this
        }

        fun setTokenPoint(tokenPoint: String) : Builder {
            oAuthManager.tokenEndPoint = tokenPoint
            return this
        }

        fun setClientId(clientId: String) : Builder {
            oAuthManager.clientId = clientId
            return this
        }

        fun setRedirectUri(redirectUri: String): Builder {
            oAuthManager.redirectUrl = redirectUri
            return this
        }

        fun build(): OAuthManager {
            oAuthManager.let {
                if (it.authEndPoint != null &&
                        it.tokenEndPoint != null &&
                        it.clientId != null &&
                        it.redirectUrl != null &&
                        it.postAuthorizationIntent != null) return it

                throw IllegalArgumentException("Need more arguments")
            }
        }

    }

    fun startAuthFlow() {
        val serviceConfiguration =
            AuthorizationServiceConfiguration(
                Uri.parse(authEndPoint), /* auth endpoint */
                Uri.parse(tokenEndPoint) /* token endpoint */
            )

        val builder = AuthorizationRequest.Builder(
            serviceConfiguration,
            clientId!!,
            ResponseTypeValues.TOKEN,
            Uri.parse(redirectUrl!!)
        ).setState(null)

        builder.setScopes(scopes?.toMutableList())

        val request = builder.build()

        val pendingIntent = PendingIntent.getActivity(
            activity,
            request.hashCode(),
            postAuthorizationIntent,
            0
        )
        authorizationService.performAuthorizationRequest(request, pendingIntent)
    }

    fun dispose() {
        authorizationService.dispose()
    }

    private fun isPackageInstalled(
        packageName: String?,
        packageManager: PackageManager
    ): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}