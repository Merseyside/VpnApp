package com.merseyside.dropletapp.filemanager

import com.merseyside.dropletapp.di.appContext

actual class FileManager actual constructor() {

    actual fun getAssetContent(filename: String): String? {
        return com.merseyside.filemanager.utils.getAssetContent(appContext!!, filename)
    }

}