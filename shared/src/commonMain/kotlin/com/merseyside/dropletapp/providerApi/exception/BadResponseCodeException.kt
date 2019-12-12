package com.merseyside.dropletapp.providerApi.exception

class BadResponseCodeException(msg: String, val code: Int): Throwable(msg)