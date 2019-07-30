package com.merseyside.dropletapp.data.db

import com.squareup.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver): VpnDatabase {

    return VpnDatabase(driver)
}