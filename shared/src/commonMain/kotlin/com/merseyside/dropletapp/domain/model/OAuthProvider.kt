package com.merseyside.dropletapp.domain.model

import com.merseyside.dropletapp.providerApi.Provider

data class OAuthProvider(
    val provider: Provider,
    var token: String?,
    val oAuthConfig: OAuthConfig?
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || this::class != other::class) return false

        other as OAuthProvider

        if (provider != other.provider) return false
        if (token != other.token) return false
        if (oAuthConfig != other.oAuthConfig) return false

        return true
    }

    override fun hashCode(): Int {
        var result = provider.hashCode()
        result = 31 * result + (token?.hashCode() ?: 0)
        result = 31 * result + (oAuthConfig?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OAuthProvider(provider=$provider, token=$token, oAuthConfig=$oAuthConfig)"
    }


}