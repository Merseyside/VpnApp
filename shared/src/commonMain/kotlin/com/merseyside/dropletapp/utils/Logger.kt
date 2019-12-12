package com.merseyside.dropletapp.utils

internal expect class Logger {

    companion object {

        internal fun logMsg(tag: String, obj: Any)

        internal fun logError(tag: String, obj: Any, throwable: Throwable? = null)

        internal actual fun printStackTrace(e: Throwable)
    }
}