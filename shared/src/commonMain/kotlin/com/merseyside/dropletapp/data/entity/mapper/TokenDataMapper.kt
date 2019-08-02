package com.merseyside.dropletapp.data.entity.mapper

import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.db.model.TokenModel

class TokenDataMapper {

    internal fun transform(tokenModels: List<TokenModel>): List<TokenEntity> {
        return tokenModels.map {
            transform(it)
        }
    }

    internal fun transform(tokenModel: TokenModel): TokenEntity {
        return tokenModel.let {
            TokenEntity(
                token = it.token,
                name = it.name,
                providerId = it.providerId
            )
        }
    }
}