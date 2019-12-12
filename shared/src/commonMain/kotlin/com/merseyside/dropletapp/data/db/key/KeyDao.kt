package com.merseyside.dropletapp.data.db.key

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.db.model.KeyModel

class KeyDao(database: VpnDatabase) {

    private val db = database.keyModelQueries

    internal fun insert(
        sshKeyId: Long,
        publicKeyPath: String,
        privateKeyPath: String,
        token: String
    ) {
        db.insert(sshKeyId, publicKeyPath, privateKeyPath, token)
    }

    internal fun selectById(sshKeyId: Long): KeyModel? {
        return db.selectById(sshKeyId).executeAsOneOrNull()
    }

    companion object {
        private const val TAG = "KeyDao"
    }
}