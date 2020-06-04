package com.merseyside.dropletapp.filemanager

expect class FileManager constructor() {

    fun getAssetContent(filename: String): String?
}