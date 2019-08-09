package com.merseyside.dropletapp.ssh

expect class SshConnection(
    username: String,
    host: String,
    filePathPrivate: String,
    filePathPublic: String,
    passphrase: String) {

    fun openSshConnection(): Boolean

    fun isConnected(): Boolean

    fun closeConnection()

    fun setTimeout(timeout: Int)
}