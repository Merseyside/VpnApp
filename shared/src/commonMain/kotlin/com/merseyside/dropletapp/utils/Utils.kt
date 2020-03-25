@file:JvmName("Utils")
package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import com.soywiz.klock.DateTime
import kotlin.jvm.JvmName

fun isDropletValid(droplet: DropletInfoResponse): Boolean {
    droplet.let {
        return it.id > 0 && it.networks.isNotEmpty()
    }
}

fun generateRandomString(length: Int = 10): String {
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    return (1..length)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

fun getCurrentTimeMillis(): Long {
    return DateTime.now().milliseconds.toLong()
}

fun String.hexStringToByteArray() = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }