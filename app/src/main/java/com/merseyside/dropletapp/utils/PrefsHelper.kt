package com.merseyside.dropletapp.utils

import com.merseyside.merseyLib.utils.PreferenceManager

class PrefsHelper(private val preferenceManager: PreferenceManager) {

    fun addTrialTime(time: Long) {
        val total = getTrialTime() + time
        preferenceManager.put(TRIAL_TIME_KEY, total)
    }

    fun getTrialTime(): Long {
        return preferenceManager.getLong(TRIAL_TIME_KEY, 0L)
    }

    companion object {
        private const val TRIAL_TIME_KEY = "trial_time"
    }
}