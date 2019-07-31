package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.data.entity.Token

fun isNameValid(name: String): Boolean {
    return name.length > 2
}

fun isTokenValid(token: Token): Boolean {
    val regex = "^[A-Fa-f0-9]+$".toRegex()

    return token.length == 64 && regex.matches(token)
}