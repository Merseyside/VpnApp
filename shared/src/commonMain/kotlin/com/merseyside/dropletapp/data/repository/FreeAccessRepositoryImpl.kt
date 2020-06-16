package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.connectionTypes.Type
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

    override suspend fun getVpnConfig(type: Type): String {

        val configTime = settings.getConfigTime()

        return if (configTime == null) {
            getConfigFromApi(type)
        } else {
            val currentTime = getCurrentTimeMillis()

            val hour = Hours(1)

            if (currentTime - configTime > hour.toMillisLong()) {
                throw TrialIsOverException()
            } else {
                val savedType = settings.getConfigType()

                if (savedType != type) {
                    getConfigFromApi(type)
                } else {
                    settings.getConfig()!!
                }
            }
        }
    }

    private suspend fun getConfigFromApi(type: Type): String {
        return freeAccessApi.getConfig(type.getTypeForApi()).also { settings.setConfig(type, it) }
    }
}