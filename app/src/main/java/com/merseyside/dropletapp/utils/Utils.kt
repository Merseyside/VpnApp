package com.merseyside.dropletapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.merseyLib.presentation.ext.getString

val application = VpnApplication.getInstance()

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
        ProviderRepositoryImpl.LogStatus.SETUP -> getString(context, R.string.setting_server)
        ProviderRepositoryImpl.LogStatus.CONNECTING -> getString(context, R.string.connecting_to_server)
        ProviderRepositoryImpl.LogStatus.CHECKING_STATUS -> getString(context, R.string.checking_server)
        ProviderRepositoryImpl.LogStatus.CREATING_SERVER -> getString(context, R.string.creating_server)
        ProviderRepositoryImpl.LogStatus.SSH_KEYS -> getString(context, R.string.ssh_keys)
    }
}

@DrawableRes
fun getProviderIcon(providerId: Long): Int {
    return when(Provider.getProviderById(providerId)) {
        is Provider.DigitalOcean -> R.drawable.digital_ocean
        is Provider.Linode -> R.drawable.ic_linode
        is Provider.CryptoServers -> R.drawable.crypto_servers
        is Provider.Custom -> R.drawable.ic_custom
        null -> TODO()
    }
}

@ColorRes
fun getProviderColor(providerId: Long): Int {
    return when(Provider.getProviderById(providerId)) {
        is Provider.DigitalOcean -> R.color.digital_ocean_color
        is Provider.Linode -> R.color.linode_color
        is Provider.CryptoServers -> R.color.crypto_servers_color
        is Provider.Custom -> R.color.custom_color
        null -> TODO()
    }
}

fun generateRandomString(length: Int): String {
    if (length > 0) {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    return ""
}

fun getDrawableByName(context: Context, name: String): Drawable? {
    return ContextCompat.getDrawable(context, getDrawableResourceIdByName(context, name))
}

@DrawableRes
fun getDrawableResourceIdByName(context: Context, name: String): Int {
    val resources: Resources = context.resources
    return resources.getIdentifier(
        name, "drawable",
        context.packageName
    )
}