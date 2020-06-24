package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.easyAccess.EasyAccessApi
import com.merseyside.dropletapp.easyAccess.EasyAccessRouter
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis

class EasyAccessRepositoryImpl(
    private val easyAccessApi: EasyAccessApi,
    private val settings: SettingsHelper
) : EasyAccessRepository {

    override suspend fun getFreeVpnConfig(type: Type, regionId: String): String {
        val configTime = settings.getConfigTime()

        return if (configTime == null) {
            getConfigFromApi(type, regionId)
        } else {
            val currentTime = getCurrentTimeMillis()

            val hour = Hours(1)

            if (currentTime - configTime > hour.toMillisLong()) {
                throw TrialIsOverException()
            } else {
                val savedType = settings.getConfigType()

                if (savedType != type) {
                    getConfigFromApi(type, regionId)
                } else {
                    settings.getConfig()!!
                }
            }
        }
    }

    override suspend fun getVpnConfig(
        type: Type,
        regionId: String
    ): String {
        return getConfigFromApi(type, regionId)
    }

    override suspend fun getRegions(): List<RegionPoint> {
        return easyAccessApi.getRegions()
    }

    private suspend fun getConfigFromApi(
        type: Type,
        regionId: String
    ): String {
        return easyAccessApi.getConfig(type.getTypeForApi(), regionId)
            .also {
                settings.setConfig(type, it)
            }
    }
}