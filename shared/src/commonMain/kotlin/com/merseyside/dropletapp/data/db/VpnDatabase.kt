package com.merseyside.dropletapp.data.db

import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.db.model.ServerModel
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun createDatabase(driver: SqlDriver): VpnDatabase {

    val typedConfigAdapter = object: ColumnAdapter<TypedConfig, String> {
        override fun decode(databaseValue: String): TypedConfig {
            val json = Json.Default

            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: TypedConfig): String {
            val json = Json.Default

            return json.encodeToString(value)
        }

    }

    return VpnDatabase(driver,
        ServerModelAdapter = ServerModel.Adapter(
            typedConfigAdapter = typedConfigAdapter
        ))
}