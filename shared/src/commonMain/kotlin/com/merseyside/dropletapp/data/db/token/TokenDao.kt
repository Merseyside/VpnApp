package com.merseyside.dropletapp.data.db.token

import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.entity.mapper.TokenDataMapper
import com.merseyside.dropletapp.providerApi.Provider

class TokenDao(database: VpnDatabase) {

    private val db = database.tokenModelQueries
    private val tokenDataMapper = TokenDataMapper()

    internal fun insert(
        token: Token,
        name: String,
        provider: Provider
    ) {
        db.insertItem(token, name, provider.getId())
    }

    internal fun selectByServiceId(providerId: Long): List<TokenEntity> {
        return tokenDataMapper.transform(db.selectByServiceId(providerId).executeAsList())
    }

    internal fun getAllTokens(): List<TokenEntity> {
        return tokenDataMapper.transform(db.selectAll().executeAsList())
    }

    internal fun deleteToken(token: Token) {
        db.deleteItem(token)
    }

    companion object {
        private const val TAG = "TokenDao"
    }
}