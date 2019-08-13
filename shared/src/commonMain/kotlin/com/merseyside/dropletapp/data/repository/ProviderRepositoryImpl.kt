package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import com.merseyside.dropletapp.ssh.SshConnection
import com.merseyside.dropletapp.utils.DROPLET_TAG
import com.merseyside.dropletapp.utils.Logger
import com.merseyside.dropletapp.utils.isDropletValid
import kotlinx.coroutines.delay

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

        repeat(REPEAT_COUNT) {

            val createDropletResponse = provider.createDroplet(
                token = token,
                name = "vpn-$serverName",
                regionSlug = regionSlug,
                sshKeyId = keyResponse.id,
                tag = DROPLET_TAG
            )

            Logger.logMsg(TAG, "Trying $it...")

            if (createDropletResponse.id <= 0L) throw IllegalStateException("Error while creating droplet")

            val infoResponse = provider.getDropletInfo(token, createDropletResponse.id)

            if (isDropletValid(infoResponse)) {

                val sshConnection = setupServer(
                    "root",
                    infoResponse.networks.first().ipAddress,
                    keyPair.second.keyPath,
                    keyPair.first.keyPath
                )

                infoResponse.let { info ->
                    serverDao.insert(
                        id = info.id,
                        token = token,
                        providerId = providerId,
                        name = info.name,
                        sshKeyId = keyResponse.id,
                        serverStatus = info.status,
                        environmentStatus = sshConnection?.let {SshManager.Status.IN_PROCESS.toString()} ?: return@repeat,
                        createdAt = info.createdAt,
                        regionName = info.regionName,
                        networks = info.networks
                    )
                }

                keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

                return true
            } else {
                if (it != REPEAT_COUNT - 1) {
                    deleteDroplet(token, providerId, createDropletResponse.id)
                    delay(DELAY_MILLIS)
                }
            }
        }

        throw IllegalArgumentException("Can't create valid droplet")

    }

    private suspend fun setupServer(
        hostName: String = "root",
        ipAddress: String,
        keyPathPrivate: String,
        keyPathPublic: String): SshConnection? {

        return sshManager.setupServer(
            hostName,
            ipAddress,
            keyPathPrivate,
            keyPathPublic
        )
    }

    override suspend fun getServers(): List<Server> {
        return serverDao.getAllDroplets().map {
            Server(
                id = it.id,
                token = it.token,
                name = it.name,
                createdAt = it.createdAt,
                regionName = it.regionName,
                providerId = it.providerId,
                providerName = Provider.getProviderById(it.providerId)?.getName() ?: throw IllegalArgumentException("No providerName found with this id"),
                serverStatus = it.serverStatus,
                environmentStatus = SshManager.Status.getStatusByString(it.environmentStatus) ?: throw IllegalArgumentException("Wrong status name")
            )
        }
    }


    override suspend fun deleteDroplet(token: Token, providerId: Long, dropletId: Long): Boolean {
        Logger.logMsg(TAG, "Delete droplet")
        val provider = providerApiFactory.getProvider(providerId)

        provider.deleteDroplet(token, dropletId)

        serverDao.deleteDroplet(dropletId, providerId)

        return true
    }

    override suspend fun getOvpnFile(dropletId: Long, providerId: Long): String {
        val server = serverDao.getDropletByIds(dropletId, providerId)

        if (server != null) {
            val keys = keyDao.selectById(server.sshKeyId)

            return sshManager.getOvpnFile(
                "root",
                server.networks.networkList.first().ipAddress,
                keys?.privateKeyPath ?: throw NoDataException(),
                keys.publicKeyPath)


        } else throw NoDataException("No droplet with passed id")
    }

    companion object {

        private const val REPEAT_COUNT = 8
        private const val DELAY_MILLIS = 2000L

        private const val TAG = "ProviderRepository"

        private val providers: List<Provider> = Provider.getAllServices()

    }
}