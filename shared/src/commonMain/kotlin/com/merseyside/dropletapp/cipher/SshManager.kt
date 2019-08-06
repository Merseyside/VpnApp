package com.merseyside.dropletapp.cipher

expect class SshManager constructor() {

    fun createRsaKeys(): Pair<String, String>?
}