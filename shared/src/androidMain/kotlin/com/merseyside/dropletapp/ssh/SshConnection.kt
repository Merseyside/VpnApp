package com.merseyside.dropletapp.ssh

import android.util.Log
import com.jcraft.jsch.*

actual class SshConnection actual constructor(
    private val username: String,
    private val host: String,
    private val filePath: String,
    private val passphrase: String
) {

    private var session: Session? = null
    private var channel: Channel? = null

    interface SshConnectionListener {

    }

    actual fun openSshConnection(): Boolean {
        try {
            val jsch = JSch()

            jsch.addIdentity(filePath)

            session = jsch.getSession(username, host, 22)

            val userInfo = MyUserInfo()
            session!!.userInfo = userInfo
            session!!.connect()

            channel = session!!.openChannel("shell")
            channel!!.inputStream

            channel!!.connect()

            return true
        } catch (e: Exception) {
            e.printStackTrace()

            return false
        }
    }

    actual fun isConnected(): Boolean {
        return session?.isConnected ?: false
    }

    actual fun closeConnection() {
        session?.disconnect()
    }

    inner class MyUserInfo : UserInfo {
        override fun promptPassphrase(p0: String?): Boolean {
            return true
        }

        override fun getPassphrase(): String {
            return this@SshConnection.passphrase
        }

        override fun getPassword(): String? {
            return null
        }

        override fun promptYesNo(p0: String?): Boolean {
            return false
        }

        override fun showMessage(message: String?) {
            Log.d(TAG, message)
        }

        override fun promptPassword(p0: String?): Boolean {
            return false
        }

    }

    companion object {
        private const val TAG = "SshConnection"
    }
}