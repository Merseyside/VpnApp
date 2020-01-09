package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.data.exception.BannedAddressException
import com.merseyside.dropletapp.data.exception.NoDataException
import com.merseyside.dropletapp.data.exception.NonValidToken
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.base.networkContext
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.domain.repository.TokenRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.ssh.ConnectionType
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
    private val serverDao: ServerDao,
    private val tokenRepository: TokenRepository,
    private val oAuthProviderRepository: OAuthProviderRepository
) : ProviderRepository, CoroutineScope {

    enum class LogStatus {SSH_KEYS, CREATING_SERVER, CHECKING_STATUS, CONNECTING, SETUP}

    interface LogCallback {
        fun onLog(log: LogStatus)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Logger.logError(TAG, throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = networkContext + coroutineExceptionHandler

    override suspend fun getProviders(): List<Provider> {
        return providers
    }

    override suspend fun getTypedConfigNames(): List<String> {
        return TypedConfig.getNames()
    }

    override suspend fun getProvidersWithToken(): List<Provider> {
        val tokens = tokenRepository.getAllTokens()

        return providers.mapNotNull {provider ->
            if (tokens.any { token -> token.providerId == provider.getId() }) {
                provider
            } else {
                null
            }
        }
    }

    init {
        startCheckingConfigFile()
        startCheckingServerStatus()
    }

    private fun startCheckingConfigFile() {
        launch {
            while(true) {
                delay(10000)
                val inProcessServers = serverDao.getByStatus(SshManager.Status.IN_PROCESS)

                inProcessServers.forEach { server ->
                    try {
                        getTypedConfig(server)
                    } catch(e: NoDataException) {
                        serverDao.updateStatus(
                            server.id,
                            server.providerId,
                            SshManager.Status.ERROR.toString()
                        )
                    }
                }

                yield()
            }
        }
    }

    private fun startCheckingServerStatus() {
        launch {
            while(true) {
                delay(10000)
                val inProcessServers = serverDao.getByStatus(SshManager.Status.STARTING)

                inProcessServers.forEach { server ->

                    if (isServerAlive(getToken(server.providerId), server.providerId, server.id)) {
                        serverDao.updateStatus(
                            server.id,
                            server.providerId,
                            SshManager.Status.PENDING.toString()
                        )
                    }
                }

                yield()
            }
        }
    }

    override suspend fun getRegions(providerId: Long): List<RegionPoint> {
        val provider = providerApiFactory.getProvider(providerId)

        return provider.getRegions(getToken(providerId))
    }

    private fun createKeyPair(): Pair<PublicKey, PrivateKey> {
        return sshManager.createRsaKeys() ?: throw IllegalArgumentException()
    }

    private fun getConnectionType(typedConfig: TypedConfig): ConnectionType {
        return when(typedConfig) {
            is TypedConfig.OpenVpn -> ConnectionType.OpenVpnType(typedConfig.userName)
            is TypedConfig.WireGuard -> ConnectionType.WireGuardType()
            is TypedConfig.L2TP -> ConnectionType.L2TPType(typedConfig.userName, typedConfig.password, typedConfig.key)
            is TypedConfig.PPTP -> ConnectionType.PPTPType(typedConfig.userName, typedConfig.password)
        }
    }

    private fun getTypedConfigByName(
        name: String,
        username: String
    ): TypedConfig {
            return when (name) {
                TypedConfig.OpenVpn.name -> TypedConfig.OpenVpn(username)
                TypedConfig.WireGuard.name -> TypedConfig.WireGuard()
                TypedConfig.L2TP.name -> TypedConfig.L2TP(username, generateRandomString(), generateRandomString())
                TypedConfig.PPTP.name -> TypedConfig.PPTP(username, generateRandomString())
                else -> throw IllegalStateException()
            }
    }


    override suspend fun createServer(dropletId: Long, providerId: Long, logCallback: LogCallback?): Boolean {
        logCallback?.onLog(LogStatus.CHECKING_STATUS)

        val server = serverDao.getDropletByIds(dropletId, providerId)

        if (server != null) {
            val keys = keyDao.selectById(server.sshKeyId)

            val connectionType = getConnectionType(server.typedConfig)
            val result = setupServer(
                providerId = providerId,
                username = server.username,
                ipAddress = server.address,
                keyPathPrivate = keys?.privateKeyPath ?: throw NoDataException(),
                connectionType = connectionType,
                logCallback = logCallback
            )

            if (result) {
                val status = if (connectionType.isNeedsConfig()) {
                    SshManager.Status.IN_PROCESS.toString()
                } else {
                    SshManager.Status.READY.toString()
                }
                serverDao.updateStatus(dropletId, providerId, status)
            } else {
                serverDao.updateStatus(
                    server.id,
                    server.providerId,
                    SshManager.Status.ERROR.toString()
                )
            }

            return result
        }

        return false
    }

    private suspend fun getToken(providerId: Long): String {
        val oAuthProvider = oAuthProviderRepository.getOAuthProvider(Provider.getProviderById(providerId)!!)

        if (oAuthProvider.token.isNullOrEmpty()) throw NonValidToken(Provider.getProviderById(providerId)!!)

        return oAuthProvider.token!!
    }

    override suspend fun createServer(
        providerId: Long,
        regionSlug: String,
        typeName: String,
        logCallback: LogCallback?
    ): Boolean {

        logCallback?.onLog(LogStatus.SSH_KEYS)

        val token = getToken(providerId)

        val keyPair = createKeyPair()

        val providerApi = providerApiFactory.getProvider(providerId)
        val keyResponse = providerApi.importKey(token, "My VPN ssh key", keyPair.first.key)

        logCallback?.onLog(LogStatus.CREATING_SERVER)

        val createDropletResponse = providerApi.createDroplet(
            token = token,
            name = "vpn-${generateRandomString()}",
            regionSlug = regionSlug,
            sshKeyId = keyResponse.id,
            sshKey = keyPair.first.key,
            tag = DROPLET_TAG
        )

        if (isDropletValid(createDropletResponse)) {

            val address = createDropletResponse.networks.first().ipAddress

            val username = generateRandomString()

            val typeConfig = getTypedConfigByName(typeName, username)

            createDropletResponse.let { info ->
                serverDao.insert(
                    id = info.id,
                    username = username,
                    providerId = providerId,
                    name = info.name,
                    sshKeyId = keyResponse.id,
                    serverStatus = info.status,
                    environmentStatus = SshManager.Status.STARTING.toString(),
                    createdAt = info.createdAt,
                    regionName = info.regionName,
                    address = address,
                    typedConfig = typeConfig
                )
            }

            keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

//            logCallback?.onLog("Connecting to server by SSH...")
//
//            val connectionType = getConnectionType(typeConfig)
//            val result = setupServer(
//                providerId = providerId,
//                username = username,
//                ipAddress = address,
//                keyPathPrivate = keyPair.second.keyPath,
//                connectionType = connectionType,
//                logCallback = logCallback
//            )
//
//            if (result) {
//                val status = if (connectionType.isNeedsConfig()) {
//                    SshManager.Status.IN_PROCESS.toString()
//                } else {
//                    SshManager.Status.READY.toString()
//                }
//                serverDao.updateStatus(createDropletResponse.id, providerId, status)
//            } else {
//                deleteDroplet(providerId, createDropletResponse.id)
//
//                throw BannedAddressException()
//            }
//
            return true
        }

        throw IllegalArgumentException("Can't create valid droplet. Please try again")
    }

    private suspend fun setupServer(
        providerId: Long,
        username: String,
        ipAddress: String,
        keyPathPrivate: String,
        connectionType: ConnectionType,
        logCallback: LogCallback? = null): Boolean {
        val provider = Provider.getProviderById(providerId)

        val preScriptTimeSeconds = if (provider is Provider.CryptoServers) {
            30
        } else {
            null
        }

        return sshManager.setupServer(
            username,
            ipAddress,
            keyPathPrivate,
            connectionType,
            preScriptTimeSeconds,
            logCallback
        )
    }

    override fun getDropletsFlow(): Flow<List<Server>> {
        return serverDao.getAllDroplets().map {
            it.map {server ->
                Server(
                    id = server.id,
                    name = server.name,
                    createdAt = server.createdAt,
                    regionName = server.regionName,
                    providerId = server.providerId,
                    providerName = Provider.getProviderById(server.providerId)?.getName() ?: throw IllegalArgumentException(
                        "No providerName found with this id"
                    ),
                    serverStatus = server.serverStatus,
                    address = server.address,
                    typedConfig = server.typedConfig,
                    environmentStatus = SshManager.Status.getStatusByString(server.environmentStatus)
                        ?: throw IllegalArgumentException("Wrong status name")
                )
            }
        }
    }


    override suspend fun deleteDroplet(providerId: Long, dropletId: Long): Boolean {
        Logger.logMsg(TAG, "Delete droplet")
        val provider = providerApiFactory.getProvider(providerId)

        try {
            provider.deleteDroplet(getToken(providerId), dropletId)
        } catch (e: Exception) {}

        serverDao.deleteDroplet(dropletId, providerId)

        return true
    }

    override suspend fun getTypedConfig(server: ServerModel): TypedConfig {

        val keys = keyDao.selectById(server.sshKeyId)

        if (server.typedConfig.config.isNullOrEmpty()) {

            val file = sshManager.getConfigFile(
                server.username,
                server.address,
                keys?.privateKeyPath ?: throw NoDataException(),
                getConnectionType(server.typedConfig)
            )

            server.typedConfig.config = file

            if (file != null && file.length > 200) {
                serverDao.updateStatus(
                    server.id,
                    server.providerId,
                    SshManager.Status.READY.toString()
                )
                serverDao.addTypedConfig(server.id, server.providerId, server.typedConfig)
            } else throw NoDataException("No ovpn file found")

            return server.typedConfig
        } else {
            return server.typedConfig
        }
    }

    private suspend fun isServerAlive(token: String, providerId: Long, dropletId: Long): Boolean {
        val provider = providerApiFactory.getProvider(providerId)

        return provider.isServerAlive(token, dropletId)
    }

    private fun generateRandomString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..10)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    companion object {

        private const val TAG = "ProviderRepository"

        val providers: List<Provider> = Provider.getAllServices()

    }
}