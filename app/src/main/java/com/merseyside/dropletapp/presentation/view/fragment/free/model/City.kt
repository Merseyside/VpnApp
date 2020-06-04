package com.merseyside.dropletapp.presentation.view.fragment.free.model

data class City(
    val name: String,
    val country: String,
    val connectionLevel: Int,
    val isLocked: Boolean
)