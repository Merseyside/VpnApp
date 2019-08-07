package com.merseyside.dropletapp.ssh

import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey

expect class SshManager constructor() {

    fun createRsaKeys(): Pair<PublicKey, PrivateKey>?

    fun openSshConnection(
        username: String,
        host: String,
        filePath: String
    ): Boolean

    fun closeConnection(connection: SshConnection)
}