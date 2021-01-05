package com.merseyside.dropletapp.presentation.exception

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.merseyside.archy.presentation.interfaces.IStringHelper
import com.merseyside.dropletapp.BuildConfig
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.data.exception.BannedAddressException
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.providerApi.exception.BadResponseCodeException
import com.merseyside.dropletapp.providerApi.exception.InvalidTokenException

class ErrorMessageFactory : IStringHelper {

    fun createErrorMsg(throwable: Throwable): String {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
        }

        return if (throwable is BadResponseCodeException) {
            "Code: ${throwable.code}. ${throwable.message}"
        } else if (throwable is NoDataException) {
            getString(R.string.no_data_msg)
        } else if (throwable is InvalidTokenException) {
            throwable.message ?: getString(R.string.unknown_error_msg)
        } else if (throwable is SQLiteConstraintException) {
            getString(R.string.already_added_msg)
        } else if (throwable is BannedAddressException) {
            throwable.message ?: getString(R.string.banned_msg)
        } else {
            throwable.message ?: getString(R.string.unknown_error_msg)
        }

    }

    companion object {
        private const val TAG = "ErrorMessageFactory"
    }

    override fun getLocaleContext(): Context {
        return VpnApplication.getInstance()
    }
}