package com.merseyside.dropletapp.data.exception

import com.merseyside.dropletapp.providerApi.Provider

class NonValidToken(provider: Provider, msg: String? = null) : Exception(msg)