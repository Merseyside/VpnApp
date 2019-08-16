package com.merseyside.dropletapp.presentation.exception

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.merseyside.dropletapp.BuildConfig
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.providerApi.exception.BadResponseCodeException
import com.merseyside.dropletapp.providerApi.exception.InvalidTokenException

class ErrorMessageFactory(private val context: Context) {

    fun createErrorMsg(throwable: Throwable): String {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        return if (throwable is BadResponseCodeException) {
            "Code: ${throwable.code}. ${throwable.message}"
        } else if (throwable is NoDataException) {
            "No data"
        } else if (throwable is InvalidTokenException) {
            throwable.message ?: getString(R.string.unknown_error_msg)
        } else if (throwable is SQLiteConstraintException) {
            "Already added"
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