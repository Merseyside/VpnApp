package com.merseyside.dropletapp.domain.model

expect class OAuthConfig(
    authEndPoint: String,
    tokenEndPoint: String,
    clientId: String,
    redirectUrl: String,
    scopes: String
) {

    var authEndPoint: String
    var tokenEndPoint: String
    var clientId: String
    var redirectUrl: String
    var scopes: String

    class Builder(jsonFilename: String) {
        fun build(): OAuthConfig?
    }
}
