package com.merseyside.dropletapp.providerApi.base.entity.response

data class CreateSshKeyResponse(
    val id: Long,
    val fingerprint: String
)