package com.merseyside.dropletapp.providerApi.amazon

expect fun getRequest(
    method: String,
    service: String = "lightsail",
    host: String = "lightsail.us-east-1.amazonaws.com",
    region: String = "us-east-1",
    endpoint: String = "https://lightsail.us-east-1.amazonaws.com",
    action: String,
    accessKey: String,
    secretKey: String
): Pair<String, Map<String, String>>