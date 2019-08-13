package com.merseyside.dropletapp.ssh

import android.util.Log
import com.jcraft.jsch.*
import com.merseyside.admin.merseylibrary.data.filemanager.FileManager
import java.lang.IllegalStateException
import com.jcraft.jsch.ChannelExec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*


actual class SshConnection actual constructor(
    val username: String,
    val host: String,
    val filePathPrivate: String,
    val filePathPublic: String,
    val passphrase: String
) {

    private var session: Session? = null
    private var channel: ChannelExec? = null

    private val randomUsername by lazy { generateRandomString() }

    private var timeout = 0

    interface SshConnectionListener {

    }

    actual fun openSshConnection(): Boolean {
        try {
            if (isConnected()) return true

            val jsch = JSch()

            Log.d(TAG, filePathPrivate)

            jsch.addIdentity(filePathPrivate, filePathPublic)

            session = jsch.getSession(username, host, 22)

//            val userInfo = MyUserInfo()
//            session!!.userInfo = userInfo

            if (timeout != 0) {
                session!!.timeout = timeout
            }

            val prop = Properties()
            prop["StrictHostKeyChecking"] = "no"

            session!!.setConfig(prop)
            session!!.connect()

            return true
        } catch (e: Exception) {
            e.printStackTrace()

            return false
        }
    }

    actual fun setupServer(): Boolean {
        if (isConnected()) {

            if (channel == null) {
                channel = session!!.openChannel("exec") as ChannelExec
                channel!!.connect()
            }

            (channel as ChannelExec).setCommand(getSetupScript())

            val str = FileManager.convertStreamToString(channel!!.inputStream)

            return true

        } else throw IllegalStateException("Server is not connected")
    }

    actual fun isConnected(): Boolean {
        return session?.isConnected ?: false
    }

    actual fun closeConnection() {
        channel?.disconnect()
        session?.disconnect()

        channel = null
        session = null
    }

    actual fun setTimeout(timeout: Int) {
        if (timeout > 0) {
            this.timeout = timeout
        }
    }

    actual fun getOvpnFile(): String {
        channel = session!!.openChannel("exec") as ChannelExec

        channel!!.setCommand(getOvpnFileScript())

        channel!!.setPty(true)
//        channel!!.setCommand(getOvpnFileScript())
//        channel!!.setCommand(getOvpnFileScript())
//        channel!!.setCommand(getOvpnFileScript())
//
//
//        val tmp = ByteArray(1024)
//        val inputStream = ByteArrayInputStream(tmp, 0, 1024)
//        channel!!.inputStream = inputStream
//
        channel!!.connect()
//
        Thread.sleep(7_000)
//
//        channel!!.disconnect()
//        session!!.disconnect()

        //return FileManager.convertStreamToString(inputStream)

        //channel!!.outputStream.write(getOvpnFileScript().toByteArray())

        val tmp = ByteArray(1024)
        while (true) {
            while (channel!!.inputStream.available() > 0) {
                Log.d(TAG, "here")
                val i = channel!!.inputStream.read(tmp, 0, 1024)
                if (i < 0) break
                Log.d("Output", String(tmp, 0, i))
            }
            if (channel!!.isClosed) {
                if (channel!!.inputStream.available() > 0) continue
                Log.d("Output", "exit-status: " + channel!!.exitStatus)
                break
            }
            try {
                Thread.sleep(1000)
            } catch (ee: Exception) {
            }

        }

        return "kek"

        //channel!!.outputStream.write(getOvpnFileScript().toByteArray())

        //val str = FileManager.convertStreamToString(channel!!.inputStream)
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

    private fun getSetupScript(): String {
        return "pwd"

//        return "`export CLIENT=$randomUsername" +
//                " && bash -c " +
//                "\"\$(wget https://gist.githubusercontent.com/myvpn-run/ab573e451a7b44991fb3a45" +
//                "66496d0f0/raw/4b9aa9f10049f1350fd81e1d1e4350b5bb227c7e/openvpn.sh -O -)\"" +
//                " && cat /root/$randomUsername.ovpn`"
    }

    private fun getOvpnFileScript(): String {
        //return "cat /root/$randomUsername.ovpn"
        return "ls"
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
    }


}