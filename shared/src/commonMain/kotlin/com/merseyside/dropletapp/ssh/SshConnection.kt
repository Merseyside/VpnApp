package com.merseyside.dropletapp.ssh

expect class SshConnection(
    username: String,
    host: String,
    filePath: String,
    passphrase: String) {

    fun openSshConnection(): Boolean

    fun isConnected(): Boolean

    fun closeConnection()
}