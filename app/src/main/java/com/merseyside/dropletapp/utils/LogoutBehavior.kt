package com.merseyside.dropletapp.utils

import android.app.Activity
import com.merseyside.dropletapp.presentation.view.activity.browser.BrowserActivity

class LogoutBehavior(
    private val activity: Activity,
    private val uri: String) {

    fun start() {
        val intent = BrowserActivity.getLogoutFlowIntent(activity, uri)

        activity.startActivity(intent)
    }

}