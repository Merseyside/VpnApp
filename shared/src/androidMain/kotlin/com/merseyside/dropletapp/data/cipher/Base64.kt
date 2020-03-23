package com.merseyside.dropletapp.data.cipher

import android.util.Base64.DEFAULT
import android.util.Base64

actual class Base64 {
    actual fun encode(bytes: ByteArray): ByteArray {
        return Base64.encode(bytes, DEFAULT)
    }

    actual fun decode(data: String): ByteArray {
        return Base64.decode(data, DEFAULT)
    }

    @OptIn(ExperimentalStdlibApi::class)
    actual fun decodeToStr(data: String): String {
        return Base64.decode(data, DEFAULT).decodeToString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    actual fun decodeToStr(data: ByteArray): String {
        return Base64.decode(data, DEFAULT).decodeToString()
    }

}