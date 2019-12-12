package com.merseyside.dropletapp.data.entity

data class PublicKey(override val key: String, override val keyPath: String): CipherKey()