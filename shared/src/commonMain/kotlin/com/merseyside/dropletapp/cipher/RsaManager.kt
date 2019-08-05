package com.merseyside.dropletapp.cipher

expect class RsaManager constructor() {

    fun createRsaKeys(): Pair<String, String>
}