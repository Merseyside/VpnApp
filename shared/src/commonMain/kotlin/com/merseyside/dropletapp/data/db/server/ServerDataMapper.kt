package com.merseyside.dropletapp.data.db.server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ServerDataMapper {

    fun transform(databaseValue: String): NetworkEntity {
        return Json.Default.decodeFromString(databaseValue)
    }

    fun transform(entity: NetworkEntity): String {
        return Json.Default.encodeToString(entity)
    }
}