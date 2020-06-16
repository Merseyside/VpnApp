package com.merseyside.dropletapp.connectionTypes

import android.content.Context
import android.content.Intent
import android.net.VpnService

actual object VpnHelper {

    private var isInit = false
    private var preparationIntent: Intent? = null

    actual fun isPrepared(): Boolean {
        return prepare() == null
    }

    @Throws(IllegalStateException::class)
    fun prepare(context: Context? = null): Intent? {
        return if (context != null) {
            isInit = true

            preparationIntent = VpnService.prepare(context)
            preparationIntent
        } else {
            if (!isInit) throw IllegalStateException("You have to call prepare(context) firstly")
            else preparationIntent
        }
    }
}