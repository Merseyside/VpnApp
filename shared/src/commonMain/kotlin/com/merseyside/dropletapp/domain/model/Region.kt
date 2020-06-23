package com.merseyside.dropletapp.domain.model

data class Region(
    val id: String,
    val name: String,
    val code: String,
    val connectionLevel: Int,
    val isLocked: Boolean
)