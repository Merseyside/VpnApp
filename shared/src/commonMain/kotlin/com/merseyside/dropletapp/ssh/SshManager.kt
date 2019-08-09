package com.merseyside.dropletapp.ssh

import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey

expect class SshManager(timeoutMillis: Int) {

    enum class Status{
        PENDING, IN_PROCESS, READY;

        companion object {
            fun getStatusByString(status: String): Status?
        }
    }

    fun createRsaKeys(): Pair<PublicKey, PrivateKey>?

    suspend fun openSshConnection(
        username: String,
        host: String,
        filePathPrivate: String,
        filePathPublic: String
    ): Boolean

    fun closeConnection(connection: SshConnection)
}