package com.merseyside.dropletapp.connectionTypes

import com.merseyside.dropletapp.data.entity.TypedConfig

expect class Builder constructor() {

    fun setType(type: Type): Builder

    fun setConfig(config: String): Builder

    fun setTypedConfig(typeConfig: TypedConfig): Builder

    fun build(): ServiceConnectionType
}