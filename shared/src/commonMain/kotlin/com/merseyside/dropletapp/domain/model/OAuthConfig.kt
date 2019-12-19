package com.merseyside.dropletapp.domain.model

expect class OAuthConfig(
    authEndPoint: String,
    tokenEndPoint: String,
    clientId: String,
    redirectUrl: String
) {

    var authEndPoint: String
    var tokenEndPoint: String
    var clientId: String
    var redirectUrl: String

    class Builder(jsonFilename: String) {
        fun build(): OAuthConfig?
    }
}
