package com.merseyside.dropletapp.connectionTypes.typeImpl.shadowsocks

import com.github.shadowsocks.database.Profile
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.kmpMerseyLib.utils.serialization.deserialize

object ConfigDataMapper {

    fun transform(json: String): Profile {
        val response: TypedConfig.Shadowsocks.ShadowsocksResponse = json.deserialize()

        return Profile(
            name = "random",
            host = response.server,
            remotePort = response.port.toInt(),
            password = response.password,
            method = response.method,
            plugin = if (response.plugin != null) "$pluginName;${response.pluginOpts}" else ""
        )
    }

    const val pluginName = "v2ray"
}