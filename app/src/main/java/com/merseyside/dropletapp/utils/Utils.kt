package com.merseyside.dropletapp.utils

import android.content.Context
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl

fun isServerNameValid(name: String?): Boolean {
    return name?.let {
        val regex = "^[A-Za-z0-9]+$".toRegex()

        name.length > 2 && regex.matches(name)
    } ?: false
}

fun isNameValid(name: String?): Boolean {
    return name?.let {
        name.length > 2
    } ?: false
}

fun isTokenValid(token: Token?): Boolean {

    return token?.let {
        val regex = "^[A-Za-z0-9]+$".toRegex()

        token.isNotEmpty() && regex.matches(token)
    } ?: false

}

fun isKeyValid(key: String?): Boolean {
    return key?.let {
        true
    } ?: false
}

fun getLogByStatus(context: Context, status: ProviderRepositoryImpl.LogStatus): String {
    return when (status) {
        ProviderRepositoryImpl.LogStatus.SETUP -> context.getString(R.string.setting_server)
        ProviderRepositoryImpl.LogStatus.CONNECTING -> context.getString(R.string.connecting_to_server)
        ProviderRepositoryImpl.LogStatus.CHECKING_STATUS -> context.getString(R.string.checking_server)
        ProviderRepositoryImpl.LogStatus.CREATING_SERVER -> context.getString(R.string.creating_server)
        ProviderRepositoryImpl.LogStatus.SSH_KEYS -> context.getString(R.string.ssh_keys)
    }
}