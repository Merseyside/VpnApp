package com.merseyside.dropletapp.ssh

import android.util.Log
import com.merseyside.admin.merseylibrary.data.filemanager.FileManager
import net.schmizz.sshj.AndroidConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import java.net.ConnectException
import java.security.Security.insertProviderAt
import java.security.Security.removeProvider
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


actual class SshConnection actual constructor(
    val username: String,
    val host: String,
    val filePathPrivate: String
) {

    private val randomUsername = generateRandomString()

    private val ssh = SSHClient(AndroidConfig())

    init {
        removeProvider("BC")
        insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)

        this.ssh.addHostKeyVerifier(PromiscuousVerifier())
    }

    actual fun openSshConnection(): Boolean {

        Log.d(TAG, "Setting up connection")

        if (isConnected()) return true

        try {

            ssh.connect(host, 22)

            ssh.authPublickey(username, filePathPrivate)

            return true
        } catch (e: ConnectException) {
            return false
        }
    }

    private fun execCommand(command: String): Pair<Int, String> {
        if (isConnected()) {

            val session = ssh.startSession()

            val cmd = session!!.exec(command)

            try {
                //cmd.join(5, TimeUnit.SECONDS)

                cmd.join(400, TimeUnit.SECONDS)
                Log.d(TAG, "exit = ${cmd.exitStatus}")

                val output = IOUtils.readFully(cmd.inputStream).toString()

                Log.d(TAG, output)

                return cmd.exitStatus to output
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, IOUtils.readFully(cmd.inputStream).toString())

                return 1 to ""
            } finally {
                session.close()
            }
        }

        throw ConnectException("Can not connect to server")
    }

    actual fun setupServer(): Boolean {
        Log.d(TAG, "setupServer")

        val pair = execCommand(getSetupScript())

        return pair.first == OK
    }

    actual fun getOvpnFile(): String? {
        Log.d(TAG, "getOvpnFile")

        val pair = execCommand(getOvpnFileScript())

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
        ssh.timeout = timeout
    }

    private fun getSetupScript(): String {
        return "export CLIENT=$randomUsername" +
                " && bash -c " +
                "\"$(wget https://gist.githubusercontent.com/myvpn-run/ab573e451a7b44991fb3a45" +
                "66496d0f0/raw/4b9aa9f10049f1350fd81e1d1e4350b5bb227c7e/openvpn.sh -O -)\""
    }

    private fun getOvpnFileScript(): String {
        return "cat /root/$randomUsername.ovpn"
    }

    private fun generateRandomString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..10)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    companion object {
        private const val TAG = "SshConnection"

        private const val OK = 0
    }

}