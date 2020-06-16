package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.connectionTypes.Type
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

    companion object {
        private const val CONFIG_KEY = "config"
        private const val CONFIG_TIME_KEY = "config_time"
        private const val CONFIG_TYPE_KEY = "config_type"
    }
}