package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.domain.model.SubscriptionInfo
import com.merseyside.kmpMerseyLib.utils.serialization.deserialize
import com.merseyside.kmpMerseyLib.utils.serialization.serialize
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.russhwolf.settings.Settings

class SettingsHelper(private val settings: Settings) {

    fun setConfig(type: Type, config: String) {
        val timestamp = getCurrentTimeMillis()

        settings.putString(CONFIG_TYPE_KEY, type.name)
        settings.putString(CONFIG_KEY, config)

        if (getConfigTime() == null) {
            settings.putLong(CONFIG_TIME_KEY, timestamp)
        }
    }

    fun getConfig(): String? {
        return settings.getStringOrNull(CONFIG_KEY)
    }

    fun getConfigTime(): Long? {
        return settings.getLongOrNull(CONFIG_TIME_KEY)
    }

    fun getConfigType(): Type? {
        return Type.valueOf(settings.getString(CONFIG_TYPE_KEY, ""))
    }

    fun setSubscriptionInfo(info: SubscriptionInfo) {
        val json = info.serialize()

        settings.putString(TOKEN, json)
    }

    fun getSubscriptionInfo(): SubscriptionInfo? {
        return settings.getStringOrNull(TOKEN)?.deserialize<SubscriptionInfo>()?.let {
            if (it.token.isEmpty()) {
                null
            } else {
                it
            }
        } //?: SubscriptionInfo("1", "1")
    }

    fun setLocale(locale: String) {
        settings.putString(LOCALE, locale)
    }

    fun getLocale(): String {
        return settings.getString(LOCALE, "en")
    }

    fun setIpAddress(address: String) {
        settings.putString(IP_ADDRESS_KEY, address)
    }

    fun getIpAddress(): String {
        return settings.getString(IP_ADDRESS_KEY, "")
    }

    companion object {
        private const val CONFIG_KEY = "config"
        private const val CONFIG_TIME_KEY = "config_time"
        private const val CONFIG_TYPE_KEY = "config_type"
        private const val IP_ADDRESS_KEY = "address"

        private const val TOKEN = "token"
        private const val LOCALE = "locale"
    }
}