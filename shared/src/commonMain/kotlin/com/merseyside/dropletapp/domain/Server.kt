package com.merseyside.dropletapp.domain

import com.merseyside.dropletapp.ssh.SshManager

data class Server(
    val id: Long,
    val token: String,
    val name: String,
    val createdAt: String,
    val regionName: String,
    val providerId: Long,
    val providerName: String,
    val serverStatus: String,
    val environmentStatus: SshManager.Status
)