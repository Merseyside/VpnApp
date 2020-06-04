package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.repository.FreeAccessRepository
import com.merseyside.dropletapp.filemanager.FileManager
import com.merseyside.dropletapp.freeAccess.FreeAccessApi
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay

class FreeAccessRepositoryImpl(
    private val fileManager: FileManager,
    private val freeAccessApi: FreeAccessApi,
    private val settings: SettingsHelper
) : FreeAccessRepository {

    override suspend fun getVpnConfig(typeName: String): String {
        val config = settings.getConfig() ?: freeAccessApi.getConfig().also { settings.setConfig(it) }

        val configTime = settings.getConfigTime()
        val currentTime = getCurrentTimeMillis()

        val hour = Hours(1)

        if (currentTime - configTime > hour.toMillisLong()) {
            throw TrialIsOverException()
        } else {
            return config
        }
    }
}