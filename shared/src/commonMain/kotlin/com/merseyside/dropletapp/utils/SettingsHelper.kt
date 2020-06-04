package com.merseyside.dropletapp.utils

import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.russhwolf.settings.Settings

class SettingsHelper(private val settings: Settings) {

    fun setConfig(config: String) {
        val timestamp = getCurrentTimeMillis()

        settings.putString(CONFIG_KEY, config)
        settings.putLong(CONFIG_TIME_KEY, timestamp)
    }

    fun getConfig(): String? {
        return settings.getStringOrNull(CONFIG_KEY)
    }

    fun getConfigTime(): Long {
        return settings.getLong(CONFIG_TIME_KEY, 0)
    }

    companion object {
        private const val CONFIG_KEY = "config"
        private const val CONFIG_TIME_KEY = "config_time"
    }
}