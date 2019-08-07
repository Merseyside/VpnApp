package com.merseyside.dropletapp.data.entity

data class PrivateKey(override val key: String, override val keyPath: String): CipherKey()