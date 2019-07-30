package com.merseyside.dropletapp.data.db.token

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.entity.mapper.TokenDataMapper
import com.merseyside.dropletapp.data.entity.service.Service

class TokenDao(database: VpnDatabase) {

    private val db = database.tokenModelQueries
    private val tokenDataMapper = TokenDataMapper()

    internal fun insert(
        token: Token,
        name: String,
        service: Service
    ) {
        db.insertItem(token, name, service.getId())
    }

    internal fun selectByServiceId(serviceId: Long): List<TokenEntity> {
        return tokenDataMapper.transform(db.selectByServiceId(serviceId).executeAsList())
    }

    companion object {
        private const val TAG = "TokenDao"
    }
}