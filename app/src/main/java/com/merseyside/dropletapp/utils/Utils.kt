package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.data.entity.Token

fun isServerNameValid(name: String?): Boolean {
    return name?.let {
        val regex = "^[A-Za-z0-9]+$".toRegex()

        name.length > 2 && regex.matches(name)
    } ?: false
}

fun isNameValid(name: String?): Boolean {
    return name?.let {
        name.length > 2
    } ?: false
}

fun isTokenValid(token: Token?): Boolean {

    return token?.let {
        val regex = "^[A-Za-z0-9]+$".toRegex()

        token.isNotEmpty() && regex.matches(token)
    } ?: false

}

fun isKeyValid(key: String?): Boolean {
    return key?.let {
        true
    } ?: false
}