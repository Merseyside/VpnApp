package com.merseyside.dropletapp.providerApi.base.entity.response

data class ImportSshKeyResponse(
    val id: Long,
    val fingerprint: String? = null
)