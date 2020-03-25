package com.merseyside.dropletapp.data.cipher

expect class Base64 constructor() {

    fun encode(bytes: ByteArray): ByteArray

    fun decode(data: String): ByteArray
    fun decodeToStr(data: ByteArray): String
    fun decodeToStr(data: String): String
}