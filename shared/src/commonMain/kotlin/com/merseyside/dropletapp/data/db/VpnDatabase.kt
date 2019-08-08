package com.merseyside.dropletapp.data.db

import com.merseyside.dropletapp.data.db.server.NetworkEntity
import com.merseyside.dropletapp.data.db.server.ServerDataMapper
import com.merseyside.dropletapp.db.model.Server
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

private val serverDataMapper = ServerDataMapper()

fun createDatabase(driver: SqlDriver): VpnDatabase {



    val networkAdapter = object: ColumnAdapter<NetworkEntity, String> {

        override fun decode(databaseValue: String): NetworkEntity {
            return serverDataMapper.transform(databaseValue)
        }

        override fun encode(value: NetworkEntity): String {
            return serverDataMapper.transform(value)
        }

    }

    return VpnDatabase(driver, ServerAdapter = Server.Adapter(
        networksAdapter = networkAdapter
    ))
}