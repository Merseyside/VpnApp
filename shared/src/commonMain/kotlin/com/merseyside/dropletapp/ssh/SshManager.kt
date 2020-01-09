package com.merseyside.dropletapp.ssh

import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl

expect class SshManager(
    timeoutMillis: Int

) {

    enum class Status{
        STARTING, PENDING, IN_PROCESS, READY, ERROR;

        companion object {
            fun getStatusByString(status: String): Status?
        }
    }

    fun createRsaKeys(): Pair<PublicKey, PrivateKey>?

    suspend fun openSshConnection(
        username: String,
        host: String,
        keyPathPrivate: String,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): SshConnection?

    suspend fun setupServer(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType,
        preScriptTimeSeconds: Int? = null,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun getConfigFile(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType
    ): String?

    fun closeConnection(connection: SshConnection)
}