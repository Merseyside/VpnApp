package com.merseyside.dropletapp.utils

interface AccountManager {

    fun getAccountNamesByType(type: String): Array<String>

    fun getAuthToken(type: String): String?

    fun invalidateAuthToken(type: String, token: String)
}