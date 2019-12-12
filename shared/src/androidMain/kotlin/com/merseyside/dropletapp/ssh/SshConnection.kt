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
    val username: String,
    val host: String,
    private val filePathPrivate: String
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

            ssh.connect(host, 22)

            ssh.authPublickey("root", filePathPrivate)

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
                cmd.join(50, TimeUnit.SECONDS)

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
        Thread.sleep(30000)

        val pair = execCommand(getSetupScript())

        return pair.first == OK
    }

    actual fun getOvpnFile(): String? {
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
        ssh.connectTimeout = timeout
    }

    private fun getSetupScript(): String {
        return "export CLIENT=$username" +
                " && bash -c " +
                "\"$(wget https://gist.githubusercontent.com/myvpn-run/ab573e451a7b44991fb3a45" +
                "66496d0f0/raw/4b9aa9f10049f1350fd81e1d1e4350b5bb227c7e/openvpn.sh -O -)\""
    }

    private fun getOvpnFileScript(): String {
        return "cat /root/$username.ovpn"
    }

    companion object {
        private const val TAG = "SshConnection"

        private const val OK = 0
    }

}