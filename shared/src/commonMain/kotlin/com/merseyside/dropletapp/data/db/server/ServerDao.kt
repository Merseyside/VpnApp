package com.merseyside.dropletapp.data.db.server

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.utils.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServerDao(database: VpnDatabase) {

    private val db = database.serverModelQueries

    internal fun insert(
        id: Long,
        username: String,
        providerId: Long,
        name: String,
        sshKeyId: Long,
        serverStatus: String,
        environmentStatus: String,
        createdAt: String,
        regionName: String,
        address: String
    ) {
        db.insert(
            id = id,
            username = username,
            providerId = providerId,
            name = name,
            sshKeyId = sshKeyId,
            serverStatus = serverStatus,
            environmentStatus = environmentStatus,
            createdAt = createdAt,
            regionName = regionName,
            address = address
        )
    }

    internal fun getAllDroplets(): Flow<List<ServerModel>> {
        return db.selectAll().asFlow().map {
            it.executeAsList()
        }
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

    internal fun getByStatus(status: SshManager.Status): List<ServerModel> {
        return db.selectByEnvironmentStatus(status.toString()).executeAsList()
    }

    internal fun addOvpnFile(dropletId: Long, providerId: Long, ovpnFile: String) {
        db.addOvpnFile(ovpnFile, dropletId, providerId)
    }
}