package com.merseyside.dropletapp.domain

import com.merseyside.dropletapp.ssh.SshManager
import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val id: Long,
    val name: String,
    val createdAt: String,
    val regionName: String,
    val providerId: Long,
    val providerName: String,
    val serverStatus: String,
    val address: String,
    val config: String?,
    val environmentStatus: SshManager.Status
) {

    override fun equals(other: Any?): Boolean {
        if (other == null || this::class != other::class) return false

        other as Server

        if (id != other.id) return false
        if (name != other.name) return false
        if (createdAt != other.createdAt) return false
        if (regionName != other.regionName) return false
        if (providerId != other.providerId) return false
        if (providerName != other.providerName) return false
        if (serverStatus != other.serverStatus) return false
        if (address != other.address) return false
        if (config != other.config) return false
        if (environmentStatus != other.environmentStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + regionName.hashCode()
        result = 31 * result + providerId.hashCode()
        result = 31 * result + providerName.hashCode()
        result = 31 * result + serverStatus.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (config?.hashCode() ?: 0)
        result = 31 * result + environmentStatus.hashCode()
        return result
    }
}