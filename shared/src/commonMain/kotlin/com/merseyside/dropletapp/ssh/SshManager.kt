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

    fun savePrivateKey(key: String): PrivateKey

    suspend fun setupServer(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun setupCustomServer(
        username: String,
        host: String,
        port: Int,
        keyPathPrivate: String?,
        password: String?,
        script: String,
        logCallback: ProviderRepositoryImpl.LogCallback? = null
    ): Boolean

    suspend fun getConfigFile(
        username: String,
        host: String,
        keyPathPrivate: String?,
        password: String?,
        connectionType: ConnectionType
    ): String?

    fun closeConnection(connection: SshConnection)
}