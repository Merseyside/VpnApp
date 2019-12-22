package com.merseyside.dropletapp.data.db

import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.db.model.ServerModel
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify

@UseExperimental(ImplicitReflectionSerializer::class)
fun createDatabase(driver: SqlDriver): VpnDatabase {

    val typedConfigAdapter = object: ColumnAdapter<TypedConfig, String> {
        override fun decode(databaseValue: String): TypedConfig {
            val json = Json.nonstrict

            return json.parse(databaseValue)
        }

        override fun encode(value: TypedConfig): String {
            val json = Json.nonstrict

            return json.stringify(value)
        }

    }


    return VpnDatabase(driver,
        ServerModelAdapter = ServerModel.Adapter(
            typedConfigAdapter = typedConfigAdapter
        ))
}