package com.merseyside.dropletapp.utils

import io.ktor.content.TextContent
import io.ktor.http.ContentType

fun Any.jsonContent() : TextContent {
    return TextContent(this.toString(), ContentType.Application.Json)
}