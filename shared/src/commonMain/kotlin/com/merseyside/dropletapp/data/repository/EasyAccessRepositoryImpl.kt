package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.data.exception.TrialIsOverException
import com.merseyside.dropletapp.domain.model.Tunnel
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.easyAccess.EasyAccessApi
import com.merseyside.dropletapp.easyAccess.entity.point.RegionPoint
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.utils.time.Hours
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis

class EasyAccessRepositoryImpl(
    private val easyAccessApi: EasyAccessApi,
    private val settings: SettingsHelper
) : EasyAccessRepository {

    override suspend fun getFreeVpnConfig(type: Type, regionId: String): Tunnel {
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
                    getStoredTunnel()
                }
            }
        }
    }

    override suspend fun getVpnConfig(
        type: Type,
        regionId: String
    ): Tunnel {
        return getConfigFromApi(type, regionId)
    }

    override suspend fun getRegions(): List<RegionPoint> {
        return easyAccessApi.getRegions()
    }

    private suspend fun getConfigFromApi(
        type: Type,
        regionId: String
    ): Tunnel {
        return easyAccessApi.getTunnel(type.getTypeForApi(), regionId)
            .let { tunnel ->
                settings.apply {
                    setConfig(type, tunnel.data.access.config)
                    setIpAddress(tunnel.data.network.ip)
                }

                getStoredTunnel()
            }
    }

    private fun getStoredTunnel(): Tunnel {
        return Tunnel(settings.getConfig()!!, settings.getIpAddress())
    }
}