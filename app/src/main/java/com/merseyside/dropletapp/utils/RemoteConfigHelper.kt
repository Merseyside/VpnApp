package com.merseyside.dropletapp.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.merseyside.dropletapp.BuildConfig
import com.merseyside.dropletapp.R

class RemoteConfigHelper {

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val time = if (BuildConfig.DEBUG) {
            0
        } else {
            cacheExpiration
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(time)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        getCheating()
    }

    private fun getCheating() {

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if (firebaseRemoteConfig.getBoolean(CHEATING_KEY)) throw RuntimeException()
            }
        }
    }

    companion object {

        private const val cacheExpiration: Long = 600
        private const val CHEATING_KEY = "isCheating"
    }
}