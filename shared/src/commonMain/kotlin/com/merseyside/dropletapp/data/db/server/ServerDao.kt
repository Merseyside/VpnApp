package com.merseyside.dropletapp.data.db.server

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint

class ServerDao(database: VpnDatabase) {

    private val db = database.serverModelQueries

    internal fun insert(
        id: Long,
        token: Token,
        providerId: Long,
        name: String,
        sshKeyId: Long,
        serverStatus: String,
        environmentStatus: String,
        createdAt: String,
        regionName: String,
        networks: List<NetworkPoint>
    ) {
        db.insert(
            id = id,
            token = token,
            providerId = providerId,
            name = name,
            sshKeyId = sshKeyId,
            serverStatus = serverStatus,
            environmentStatus = environmentStatus,
            createdAt = createdAt,
            regionName = regionName,
            networks = NetworkEntity(networks)
        )
    }

    internal fun getAllDroplets(): List<ServerModel> {
        return db.selectAll().executeAsList()
    }

    internal fun deleteDroplet(dropletId: Long, providerId: Long) {
        db.delete(dropletId, providerId)
    }

    internal fun getDropletByIds(dropletId: Long, providerId: Long): ServerModel? {
        return db.selectByIds(dropletId, providerId).executeAsOneOrNull()
    }

    internal fun updateStatus(dropletId: Long, providerId: Long, status: String) {
        db.updateStatus(status, dropletId, providerId)
    }
}