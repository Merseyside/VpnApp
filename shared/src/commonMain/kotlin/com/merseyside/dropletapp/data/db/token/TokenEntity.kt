package com.merseyside.dropletapp.data.db.token

import com.merseyside.dropletapp.data.entity.Token

data class TokenEntity(
    val token: Token,
    val providerId: Long
)