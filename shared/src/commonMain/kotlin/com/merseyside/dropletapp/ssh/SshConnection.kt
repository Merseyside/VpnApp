package com.merseyside.dropletapp.ssh

expect class SshConnection(
    username: String,
    host: String,
    filePathPrivate: String) {

    fun openSshConnection(): Boolean

    fun setupServer(script: String): Boolean

    fun getConfigFile(script: String): String?

    fun isConnected(): Boolean

    fun closeConnection()

    fun setTimeout(timeout: Int)
}