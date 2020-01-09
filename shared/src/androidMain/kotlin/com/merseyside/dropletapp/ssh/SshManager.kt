package com.merseyside.dropletapp.ssh

import android.annotation.SuppressLint
import android.util.Log
import com.github.florent37.preferences.application
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import com.jcraft.jsch.Logger
import com.merseyside.admin.merseylibrary.data.filemanager.FileManager
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.repository.ProviderRepositoryImpl
import kotlinx.coroutines.delay
import java.io.File
import java.net.ConnectException

actual class SshManager actual constructor(private val timeoutMillis: Int) {

    actual enum class Status(val status: String) {
        STARTING("Staring"), PENDING("Pending"), IN_PROCESS("In Process"), READY("Ready"), ERROR("Error");

        override fun toString(): String {
            return status
        }

        actual companion object {
            actual fun getStatusByString(status: String): Status? {
                values().forEach {
                    if (it.status == status) {
                        return it
                    }
                }

                return null
            }
        }

    }

    private val jsch = JSch().also { JSch.setLogger(MyLogger()) }

    private val activeConnections = ArrayList<SshConnection>()


    actual fun createRsaKeys(): Pair<PublicKey, PrivateKey>? {
        val type = KeyPair.RSA

        val keyPair = KeyPair.genKeyPair(jsch, type)

        val outputDir = application.cacheDir

        val pubFile = File(outputDir, "rsa-${System.currentTimeMillis()}.pub")
        val priFile = File(outputDir, "rsa-${System.currentTimeMillis()}")

        keyPair.writePublicKey(pubFile.absolutePath, passphrase)
        keyPair.writePrivateKey(priFile.absolutePath)

        val privateKey = PrivateKey(FileManager.getStringFromFile(priFile.absolutePath), priFile.absolutePath)
        val publicKey = PublicKey(FileManager.getStringFromFile(pubFile.absolutePath).replace("\n", ""), pubFile.absolutePath)

        keyPair.dispose()

        return publicKey to privateKey
    }

    actual suspend fun openSshConnection(
        username: String,
        host: String,
        keyPathPrivate: String,
        logCallback: ProviderRepositoryImpl.LogCallback?
    ): SshConnection? {
        logCallback?.onLog(ProviderRepositoryImpl.LogStatus.CONNECTING)

        val connection = activeConnections.firstOrNull {
            it.host == host
        } ?: SshConnection(username, host, keyPathPrivate).also { it.setTimeout(timeoutMillis) }

        if (connection.isConnected()) {
            Log.d(TAG, "already connected")
            return connection
        }

        repeat(4) {

            if (connection.openSshConnection()) {
                return connection.also {
                    activeConnections.add(connection)
                }
            }

            delay(5000)
        }

        return null
    }

    actual suspend fun setupServer(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType,
        preScriptTimeSeconds: Int?,
        logCallback: ProviderRepositoryImpl.LogCallback?
    ): Boolean {
        val connection = openSshConnection(username, host, keyPathPrivate, logCallback) ?: return false

        logCallback?.onLog(ProviderRepositoryImpl.LogStatus.SETUP)

        if (preScriptTimeSeconds != null) {
            Thread.sleep(preScriptTimeSeconds * 1000L)
        }

        return connection.setupServer(connectionType.getSetupScript())
    }

    actual suspend fun getConfigFile(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType
    ): String? {
        val connection = openSshConnection(username, host, keyPathPrivate, null) ?: throw ConnectException("Can not connect to server")

        return connection.getConfigFile(connectionType.getConfigFileScript())?.also { Log.d(TAG, it) }
    }

    actual fun closeConnection(connection: SshConnection) {
        connection.closeConnection()
        activeConnections.remove(connection)
    }

    class MyLogger : Logger {
        override fun isEnabled(level: Int): Boolean {
            return true
        }

        override fun log(level: Int, message: String) {
            System.err.print(name[level])
            System.err.println(message)
        }

        companion object {
            @SuppressLint("UseSparseArrays")
            internal var name = HashMap<Int, String>()

            init {
                name[Logger.DEBUG] = "DEBUG: "
                name[Logger.INFO] = "INFO: "
                name[Logger.WARN] = "WARN: "
                name[Logger.ERROR] = "ERROR: "
                name[Logger.FATAL] = "FATAL: "
            }
        }
    }


    companion object {
        private const val TAG = "SshManager"

        private const val passphrase = "admin@self.host"
    }

}