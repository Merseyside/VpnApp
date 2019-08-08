package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import com.merseyside.dropletapp.utils.DROPLET_TAG
import com.merseyside.dropletapp.utils.Logger
import com.merseyside.dropletapp.utils.isDropletValid
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class ProviderRepositoryImpl(
    private val providerApiFactory: ProviderApiFactory,
    private val sshManager: SshManager,
    private val keyDao: KeyDao,
    private val serverDao: ServerDao
) : ProviderRepository {

    override suspend fun getProviders(): List<Provider> {
        return providers
    }

    override suspend fun getRegions(token: Token, providerId: Long): List<RegionPoint> {
        val provider = providerApiFactory.getProvider(providerId)

        return provider.getRegions(token)
    }

    private fun createKey(): Pair<PublicKey, PrivateKey> {
        return sshManager.createRsaKeys() ?: throw IllegalArgumentException()
    }

    override suspend fun createServer(
        token: Token,
        providerId: Long,
        regionSlug: String,
        serverName: String
    ): Boolean {
        val keyPair = createKey()

        val provider = providerApiFactory.getProvider(providerId)
        val keyResponse = provider.createKey(token, "My VPN ssh key", keyPair.first.key)

        val createDropletResponse = provider.createDroplet(
            token = token,
            name = "vpn-$serverName",
            regionSlug = regionSlug,
            sshKeyId = keyResponse.id,
            tag = DROPLET_TAG
        )

        if (createDropletResponse.id <= 0L) throw IllegalStateException("Error while creating droplet")

        val infoResponse = provider.getDropletInfo(token, createDropletResponse.id)

        val result: Boolean = withTimeout(TIMEOUT_MILLIS) {

            repeat(REPEAT_COUNT) {
                Logger.logMsg(TAG, "Trying $it...")

                if (isDropletValid(infoResponse)) {
                    keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

                    val isConnected = openSshConnection(
                        "root",
                        infoResponse.networks.first().ipAddress,
                        keyPair.second.keyPath
                    )

                    infoResponse.let { info ->
                        serverDao.insert(
                            id = info.id,
                            providerId = providerId,
                            name = info.name,
                            serverStatus = info.status,
                            environmentStatus = when(isConnected) {
                                true -> SshManager.Status.IN_PROCESS.toString()
                                false -> SshManager.Status.PENDING.toString()
                            },
                            createdAt = info.createdAt,
                            regionName = info.regionName,
                            networks = info.networks
                        )
                    }

                    return@withTimeout true
                } else {
                    if (it != REPEAT_COUNT - 1) {
                        delay(DELAY_MILLIS)
                    }
                }
            }

            false
        }

        if (!result) throw IllegalArgumentException("Droplet is not valid")
        else return result
    }

    private fun openSshConnection(
        hostName: String = "root",
        ipAddress: String,
        keyPath: String): Boolean {

        return sshManager.openSshConnection(
            hostName,
            ipAddress,
            keyPath
        )
    }

    override suspend fun getServers(): List<Server> {
        return serverDao.getAllServers().map {
            Server(
                id = it.id,
                name = it.name,
                createdAt = it.createdAt,
                regionName = it.regionName,
                providerName = Provider.getProviderById(it.providerId)?.getName() ?: throw IllegalArgumentException("No providerName found with this id"),
                serverStatus = it.serverStatus,
                environmentStatus = SshManager.Status.getStatusByString(it.environmentStatus) ?: throw IllegalArgumentException("Wrong status name")
            )
        }
    }

    companion object {

        private const val REPEAT_COUNT = 8
        private const val DELAY_MILLIS = 7000L
        private const val TIMEOUT_MILLIS = 60 * 1000L

        private const val TAG = "ProviderRepository"

        private val providers: List<Provider> = Provider.getAllServices()

    }
}