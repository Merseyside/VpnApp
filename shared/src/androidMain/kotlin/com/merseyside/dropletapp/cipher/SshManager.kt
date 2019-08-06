package com.merseyside.dropletapp.cipher

import com.github.florent37.preferences.application
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import com.merseyside.admin.merseylibrary.data.filemanager.FileManager
import java.io.File

actual class SshManager {

    actual fun createRsaKeys(): Pair<String, String>? {
        val type = KeyPair.RSA

        val jsch = JSch()

        val passphrase = "admin@self.host"

        val keyPair = KeyPair.genKeyPair(jsch, type)

        val outputDir = application.cacheDir
        val priFile = File.createTempFile("rsa-pri", "", outputDir)
        val pubFile = File.createTempFile("rsa-pub", "", outputDir)

        keyPair.writePublicKey("${pubFile.absolutePath}.pub", passphrase)
        keyPair.writePrivateKey(priFile.absolutePath, passphrase.toByteArray())

        val privateKey = FileManager.getStringFromFile(priFile.absolutePath)
        val publicKey = FileManager.getStringFromFile(pubFile.absolutePath + ".pub")

        keyPair.dispose()

        return publicKey to privateKey
    }

    companion object {
        private const val TAG = "RsaManager"
    }



}