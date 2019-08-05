package com.merseyside.dropletapp.cipher

import android.util.Base64
import android.util.Log
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException

actual class RsaManager {

    actual fun createRsaKeys(): Pair<String, String> {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)

            val keyPair = keyPairGenerator.genKeyPair()

            val pri = keyPair.private.encoded
            val pub = keyPair.public.encoded

            val privateKey = Base64.encodeToString(pri, Base64.DEFAULT)
            val publicKey = Base64.encodeToString(pub, Base64.DEFAULT)

            return publicKey to privateKey
        } catch(e: NoSuchAlgorithmException) {
            Log.e(TAG, e.message)

            throw e
        }
    }

    companion object {
        private const val TAG = "RsaManager"
    }

}