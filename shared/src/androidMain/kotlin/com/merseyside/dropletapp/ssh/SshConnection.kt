package com.merseyside.dropletapp.ssh

import android.util.Log
import net.schmizz.sshj.AndroidConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.ConnectException
import java.security.Security.insertProviderAt
import java.security.Security.removeProvider
import java.util.concurrent.TimeUnit


actual class SshConnection actual constructor(
    private val username: String,
    val host: String,
    private val port: Int,
    private val filePathPrivate: String?,
    private val password: String?
) {

    private val ssh = SSHClient(AndroidConfig())

    init {
        removeProvider("BC")
        insertProviderAt(BouncyCastleProvider(), 1)

        this.ssh.addHostKeyVerifier(PromiscuousVerifier())
    }

    actual fun openSshConnection(): Boolean {

        Log.d(TAG, "Setting up connection")

        if (isConnected()) return true

        return try {

            ssh.connect(host, port)

            when {
                filePathPrivate != null -> {
                    ssh.authPublickey(username, filePathPrivate)
                }

                password != null -> {
                    ssh.authPassword(username, password)
                }

                else -> {
                    throw IllegalStateException("Please, pass key path or password")
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()

            false
        }
    }

    private fun execCommand(command: String): Pair<Int, String> {
        if (isConnected()) {

            val session = ssh.startSession()

            Log.d(TAG, "start script")
            val cmd = session!!.exec(command)

            try {
                cmd.join(135, TimeUnit.SECONDS)

                Log.d(TAG, "exit = ${cmd.exitStatus}")

                val output = IOUtils.readFully(cmd.inputStream).toString()

                Log.d(TAG, output)

                return cmd.exitStatus to output
            } catch (e: Exception) {
                e.printStackTrace()

                return 1 to ""
            } finally {
                session.close()
            }
        }

        throw ConnectException("Can not connect to server")
    }

    actual fun setupServer(script: String): Boolean {
        Log.d(TAG, "setupServer")

        val pair = execCommand(script)

        return pair.first == OK
    }

    actual fun getConfigFile(script: String): String? {
        val pair = execCommand(script)

        return if (pair.first == OK) {
            pair.second
        } else {
            null
        }
    }

    actual fun isConnected(): Boolean {
        return ssh.isConnected
    }

    actual fun closeConnection() {
        if (isConnected()) {
            ssh.disconnect()
        }
    }

    actual fun setTimeout(timeout: Int) {
        ssh.connectTimeout = timeout
    }

    companion object {
        private const val TAG = "SshConnection"

        private const val OK = 0
    }

}