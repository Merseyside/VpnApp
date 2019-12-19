package com.merseyside.dropletapp.utils

import android.content.Context


class AccountManagerAndroid(private val context: Context) : AccountManager {

    private val accountManager = android.accounts.AccountManager.get(context)

    override fun getAccountNamesByType(type: String): Array<String> {
        return accountManager.getAccountsByType(type).map { it.name }.toTypedArray()
    }

    override fun getAuthToken(type: String): String? {
        val accounts = accountManager.getAccountsByType(type)

        if (accounts.isNotEmpty()) {
            return accountManager.blockingGetAuthToken(accounts[0], "user", true)
        }

        return null
    }

    override fun invalidateAuthToken(type: String, token: String) {
        accountManager.invalidateAuthToken(type, token)
    }


//    fun startAuth(type: String) {
//
//    }
//
//    inner class TokenRefreshCallback : AccountManagerCallback<Bundle?> {
//        fun run(result: AccountManagerFuture<Bundle?>) {
//            var bundle: Bundle? = null
//            var authIntent: Intent? = null
//            try {
//                bundle = result.result
//                authIntent =
//                    bundle?.get(AccountManager.KEY_INTENT) as Intent
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            if (authIntent != null) {
//                activity.startActivityForResult(authIntent, 11)
//                return
//            }
//            val token: String? =
//                bundle?.getString(AccountManager.KEY_AUTHTOKEN)
//
//
//            downloadCoupons(token)
//            Log.d("auth token callback ", token)
//        }
//    }
}