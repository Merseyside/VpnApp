package com.merseyside.dropletapp.data.cipher

expect class AesCipher(transformation: String = "AES/CBC/PKCS7Padding") {
    fun encrypt(data: String): String
    fun decrypt(data: String): String
    fun setKey(key: String)

    fun generateKey(length: Int): String
}