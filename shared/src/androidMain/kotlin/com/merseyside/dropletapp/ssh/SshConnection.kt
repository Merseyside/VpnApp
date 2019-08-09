package com.merseyside.dropletapp.ssh

import android.util.Log
import com.github.florent37.preferences.application
import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import com.merseyside.dropletapp.utils.readAssetFile
import java.util.*

actual class SshConnection actual constructor(
    private val username: String,
    private val host: String,
    private val filePathPrivate: String,
    private val filePathPublic: String,
    private val passphrase: String
) {

    private var session: Session? = null
    private var channel: Channel? = null

    private var timeout = 0

    interface SshConnectionListener {

    }

    actual fun openSshConnection(): Boolean {
        try {
            val jsch = JSch()

            Log.d(TAG, filePathPrivate)

            jsch.addIdentity(filePathPrivate, filePathPublic)

            session = jsch.getSession(username, host, 22)

            val userInfo = MyUserInfo()
            session!!.userInfo = userInfo

            if (timeout != 0) {
                session!!.timeout = timeout
            }

            val prop = Properties()
            prop["StrictHostKeyChecking"] = "no"

            session!!.setConfig(prop)

            session!!.connect()

            channel = session!!.openChannel("shell")
            channel!!.inputStream

            channel!!.outputStream.write(getScript().toByteArray())

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

    actual fun setTimeout(timeout: Int) {
        if (timeout > 0) {
            this.timeout = timeout
        }
    }

    inner class MyUserInfo : UserInfo {
        override fun promptPassphrase(p0: String?): Boolean {
            return false
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

    private fun getScript(): String {
        return "`export CLIENT=$username" +
                " && bash -c " +
                "\"\$(wget https://gist.githubusercontent.com/myvpn-run/ab573e451a7b44991fb3a45" +
                "66496d0f0/raw/4b9aa9f10049f1350fd81e1d1e4350b5bb227c7e/openvpn.sh -O -)\"" +
                " && cat /root/$username.ovpn`"
    }

    companion object {
        private const val TAG = "SshConnection"
    }


}