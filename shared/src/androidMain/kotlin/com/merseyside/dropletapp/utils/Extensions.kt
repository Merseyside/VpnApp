package com.merseyside.dropletapp.utils

import android.content.Context
import android.content.res.AssetManager

fun AssetManager.readAssetFile(context: Context, filename: String): String {
    return open(filename).bufferedReader().use { it.readText() }
}