@file:JvmName("Utils")
package com.merseyside.dropletapp.utils

import kotlin.jvm.JvmName

fun isIdValid(id: Long): Boolean {
    return id > 0
}