package com.merseyside.dropletapp.data.db.server

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.providerApi.base.entity.point.NetworkPoint

class ServerDao(database: VpnDatabase) {

    private val db = database.serverModelQueries

    internal fun insert(
        id: Long,
        name: String,
        status: String,
        createdAt: String,
        networks: List<NetworkPoint>
    ) {
        db.insert(
            id = id,
            name = name,
            status = status,
            createdAt = createdAt,
            networks = NetworkEntity(networks)
        )
    }
}