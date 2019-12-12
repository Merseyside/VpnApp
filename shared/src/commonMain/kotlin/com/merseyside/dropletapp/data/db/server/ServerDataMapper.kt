package com.merseyside.dropletapp.data.db.server

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify

class ServerDataMapper {

    @UseExperimental(ImplicitReflectionSerializer::class)
    fun transform(databaseValue: String): NetworkEntity {
        return Json.nonstrict.parse(databaseValue)
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    fun transform(entity: NetworkEntity): String {
        return Json.nonstrict.stringify(entity)
    }
}