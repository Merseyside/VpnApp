package com.merseyside.dropletapp.utils

import com.merseyside.utils.preferences.PreferenceManager

class PrefsHelper(private val preferenceManager: PreferenceManager) {

    fun addTrialTime(time: Long) {
        val total = getTrialTime() + time
        preferenceManager.put(TRIAL_TIME_KEY, total)
    }

    fun getTrialTime(): Long {
        return preferenceManager.getLong(TRIAL_TIME_KEY, 0L)
    }

    fun isFirstLaunch(): Boolean {
        return preferenceManager.getBool(NOT_FIRST_LAUNCH, true).also {
            preferenceManager.put(NOT_FIRST_LAUNCH, false)
        }
    }

    companion object {
        private const val NOT_FIRST_LAUNCH = "not_first_launch"
        private const val TRIAL_TIME_KEY = "trial_time"
    }
}