package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.exception.BannedAddressException
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.base.networkContext
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import com.merseyside.dropletapp.utils.DROPLET_TAG
import com.merseyside.dropletapp.utils.Logger
import com.merseyside.dropletapp.utils.isDropletValid
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext

class ProviderRepositoryImpl(
    private val providerApiFactory: ProviderApiFactory,
    private val sshManager: SshManager,
    private val keyDao: KeyDao,
    private val serverDao: ServerDao
) : ProviderRepository, CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Logger.logError(TAG, throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = networkContext + coroutineExceptionHandler

    override suspend fun getProviders(): List<Provider> {
        return providers
    }

    init {
        startCheckingStatus()
    }

    private fun startCheckingStatus() {
        launch {
            while(true) {
                delay(15000)

                val inProcessServers = serverDao.getByStatus(SshManager.Status.IN_PROCESS)

                inProcessServers.forEach {
                    getOvpnFile(it.id, it.providerId)
                }

                yield()
            }
        }
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
        val keyResponse = provider.importKey(token, "My VPN ssh key", keyPair.first.key)

        val createDropletResponse = provider.createDroplet(
            token = token,
            name = "vpn-$serverName",
            regionSlug = regionSlug,
            sshKeyId = keyResponse.id,
            tag = DROPLET_TAG
        )

        if (createDropletResponse.id <= 0L) throw IllegalStateException("Error while creating droplet")

        repeat(REPEAT_COUNT) {

            val infoResponse = provider.getDropletInfo(token, createDropletResponse.id)

            if (isDropletValid(infoResponse)) {
                val address = infoResponse.networks.first().ipAddress

                val username = generateRandomString()

                infoResponse.let { info ->
                    serverDao.insert(
                        id = info.id,
                        token = token,
                        username = username,
                        providerId = providerId,
                        name = info.name,
                        sshKeyId = keyResponse.id,
                        serverStatus = info.status,
                        environmentStatus = SshManager.Status.PENDING.toString(),
                        createdAt = info.createdAt,
                        regionName = info.regionName,
                        address = address
                    )
                }

                keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

                val sshConnection = setupServer(
                    username = username,
                    ipAddress = address,
                    keyPathPrivate = keyPair.second.keyPath
                )

                if (sshConnection) {
                    serverDao.updateStatus(infoResponse.id, providerId, SshManager.Status.IN_PROCESS.toString())
                } else {
                    deleteDroplet(token, providerId, infoResponse.id)

                    throw BannedAddressException()
                }

                return true
            } else {
                if (it == REPEAT_COUNT -1) {
                    deleteDroplet(token, providerId, infoResponse.id)
                } else {
                    delay(DELAY_MILLIS)
                }
            }
        }

        throw IllegalArgumentException("Can't create valid droplet. Please try again")
    }

    private suspend fun setupServer(
        username: String,
        ipAddress: String,
        keyPathPrivate: String): Boolean {

        return sshManager.setupServer(
            username,
            ipAddress,
            keyPathPrivate
        )
    }

    override fun getDropletsFlow(): Flow<List<Server>> {
        return serverDao.getAllDroplets().map {
            it.map {server ->
                Server(
                    id = server.id,
                    token = server.token,
                    name = server.name,
                    createdAt = server.createdAt,
                    regionName = server.regionName,
                    providerId = server.providerId,
                    providerName = Provider.getProviderById(server.providerId)?.getName() ?: throw IllegalArgumentException(
                        "No providerName found with this id"
                    ),
                    serverStatus = server.serverStatus,
                    environmentStatus = SshManager.Status.getStatusByString(server.environmentStatus)
                        ?: throw IllegalArgumentException("Wrong status name"),
                    connectStatus = false
                )
            }
        }
    }


    override suspend fun deleteDroplet(token: Token, providerId: Long, dropletId: Long): Boolean {
        Logger.logMsg(TAG, "Delete droplet")
        val provider = providerApiFactory.getProvider(providerId)

        provider.deleteDroplet(token, dropletId)

        serverDao.deleteDroplet(dropletId, providerId)

        return true
    }

    override suspend fun createServer(dropletId: Long, providerId: Long): Boolean {
        val server = serverDao.getDropletByIds(dropletId, providerId)

        if (server != null) {
            val keys = keyDao.selectById(server.sshKeyId)

            val isInProgress = setupServer(
                username = server.username,
                ipAddress = server.address,
                keyPathPrivate = keys?.privateKeyPath ?: throw NoDataException()
            )

            if (isInProgress) {
                serverDao.updateStatus(dropletId, providerId, SshManager.Status.IN_PROCESS.toString())
            }

            return isInProgress
        }

        return false
    }

    override suspend fun getOvpnFile(dropletId: Long, providerId: Long): String {
        val server = serverDao.getDropletByIds(dropletId, providerId)

        if (server != null) {
            val keys = keyDao.selectById(server.sshKeyId)

            if (server.ovpnFile.isNullOrEmpty()) {

                val file = sshManager.getOvpnFile(
                    server.username,
                    server.address,
                    keys?.privateKeyPath ?: throw NoDataException()
                )

                if (file != null) {
                    serverDao.updateStatus(dropletId, providerId, SshManager.Status.READY.toString())
                    serverDao.addOvpnFile(dropletId, providerId, file)
                } else throw NoDataException("No ovpn file found")

                return file
            } else {
                return server.ovpnFile!!
            }
        }

        throw NoDataException("No droplet with passed id")
    }

    private fun generateRandomString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..10)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    companion object {

        private const val REPEAT_COUNT = 5
        private const val DELAY_MILLIS = 7000L

        private const val TAG = "ProviderRepository"

        private val providers: List<Provider> = Provider.getAllServices()

    }
}