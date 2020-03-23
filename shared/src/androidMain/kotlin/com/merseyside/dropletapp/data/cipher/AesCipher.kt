package com.merseyside.dropletapp.data.cipher

import com.merseyside.dropletapp.utils.generateRandomString
import io.ktor.util.InternalAPI
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

actual class AesCipher actual constructor(private val transformation: String) {

    private var key: String? = null
    val IV: ByteArray = ByteArray(16)

    private val base64 = Base64()

    actual fun encrypt(data: String): String {

        if (key != null && key!!.isNotEmpty()) {
            return encryptData(data)
        } else {
            throw IllegalStateException("No key passed")
        }
    }

    actual fun decrypt(data: String): String {
        if (key != null && key!!.isNotEmpty()) {
            return decryptData(data)
        } else {
            throw IllegalStateException("No key passed")
        }
    }

    actual fun setKey(key: String) {
        if (key.isNotEmpty()) {
            this.key = key
        } else throw IllegalArgumentException("Key cannot be empty")
    }

    @Throws(Exception::class)
    fun encryptData(text: String): String {

        val cipher = Cipher.getInstance(transformation)

        val keySpec = SecretKeySpec(key!!.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val results = cipher.doFinal(text.toByteArray())

        return String(results)

    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(Exception::class)
    fun decryptData(text: String): String {

        try {
            val cipher = Cipher.getInstance(transformation)

            val keySpec = SecretKeySpec(key!!.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            return cipher.doFinal(base64.decode(text)).decodeToString()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    actual fun generateKey(length: Int): String {

        return generateRandomString(length)
    }


    companion object {
        private const val TAG = "Aes"
    }
}