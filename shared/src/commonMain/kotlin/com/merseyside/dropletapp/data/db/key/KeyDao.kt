package com.merseyside.dropletapp.data.db.key

import com.merseyside.dropletapp.data.db.VpnDatabase

class KeyDao(database: VpnDatabase) {

    private val db = database.keyModelQueries

    internal fun insert(
        sshKeyId: Long,
        publicKey: String,
        privateKey: String,
        token: String
    ) {
        db.insert(sshKeyId, publicKey, privateKey, token)
    }
}