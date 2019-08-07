@file:JvmName("Utils")
package com.merseyside.dropletapp.utils

import com.merseyside.dropletapp.providerApi.base.entity.response.DropletInfoResponse
import kotlin.jvm.JvmName

fun isDropletValid(droplet: DropletInfoResponse): Boolean {
    droplet.let {
        return it.id > 0 && it.networks.isNotEmpty()
    }
}