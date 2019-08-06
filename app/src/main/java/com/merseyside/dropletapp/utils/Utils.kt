package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.data.entity.Token

fun isNameValid(name: String?): Boolean {
    return name?.let {
        val regex = "^[A-Za-z0-9]+$".toRegex()

        name.length > 2 && regex.matches(name)
    } ?: false
}

fun isTokenValid(token: Token?): Boolean {
    return token?.let {
        val regex = "^[A-Fa-f0-9]+$".toRegex()

        token.length == 64 && regex.matches(token)
    } ?: false

}