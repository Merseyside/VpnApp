package com.merseyside.dropletapp.ssh

expect class SshConnection(
    username: String,
    host: String,
    port: Int,
    filePathPrivate: String? = null,
    password: String? = null
) {
    fun openSshConnection(): Boolean

    fun setupServer(script: String): Boolean

    fun getConfigFile(script: String): String?

    fun isConnected(): Boolean

    fun closeConnection()

    fun setTimeout(timeout: Int)
}