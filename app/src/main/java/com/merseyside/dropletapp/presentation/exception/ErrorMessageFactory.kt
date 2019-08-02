package com.merseyside.dropletapp.presentation.exception

import android.content.Context
import com.merseyside.dropletapp.BuildConfig
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.providerApi.exception.BadResponseCodeException

class ErrorMessageFactory(private val context: Context) {

    fun createErrorMsg(throwable: Throwable): String {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        return if (throwable is BadResponseCodeException) {
            "Code: ${throwable.code}. ${throwable.message}"
        } else if (throwable is NoDataException) {
            "No data"
        } else {
            throwable.message ?: getString(R.string.unknown_error_msg)
        }

    }

    private fun getString(id: Int): String {
        return context.getString(id)
    }

    companion object {
        private const val TAG = "ErrorMessageFactory"
    }
}