package com.merseyside.dropletapp.utils

import android.util.Log

internal actual class Logger {

    actual companion object {
        internal actual fun logMsg(tag: String, obj: Any) {
            Log.d(tag, obj.toString())
        }

        internal actual fun logError(tag: String, obj: Any, throwable: Throwable?) {
            Log.e(tag, obj.toString())

            throwable?.printStackTrace()
        }

        internal actual fun printStackTrace(e: Throwable) {
            e.printStackTrace()
        }
    }
}