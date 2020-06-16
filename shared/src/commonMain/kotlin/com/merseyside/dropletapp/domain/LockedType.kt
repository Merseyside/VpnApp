package com.merseyside.dropletapp.domain

import com.merseyside.dropletapp.connectionTypes.Type

data class LockedType(
    val type: Type,
    val isLocked: Boolean
)