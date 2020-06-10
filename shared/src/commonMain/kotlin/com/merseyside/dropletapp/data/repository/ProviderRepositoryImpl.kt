package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.agent.net.Agent
import com.merseyside.dropletapp.data.cipher.AesCipher
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
import com.merseyside.dropletapp.domain.repository.OAuthProviderRepository
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.domain.repository.TokenRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.base.entity.point.RegionPoint
import com.merseyside.dropletapp.ssh.ConnectionType
import com.merseyside.dropletapp.utils.*
import com.merseyside.kmpMerseyLib.domain.coroutines.computationContext
import com.merseyside.kmpMerseyLib.utils.Logger
import com.merseyside.kmpMerseyLib.utils.generateRandomString
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
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
    private val oAuthProviderRepository: OAuthProviderRepository,
    private val agent: Agent,
    private val cipher: AesCipher
) : ProviderRepository, CoroutineScope {

    enum class LogStatus {SSH_KEYS, CREATING_SERVER, CHECKING_STATUS, CONNECTING, SETUP}

    interface LogCallback {
        fun onLog(log: LogStatus)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.logErr(this, throwable)
    }

    override val coroutineContext: CoroutineContext
        get() = computationContext + coroutineExceptionHandler

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
        //startCheckingConfigFile()
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

        return provider!!.getRegions(getToken(providerId))
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
            is TypedConfig.Shadowsocks -> ConnectionType.Shadowsocks(typedConfig.password)
        }
    }

    private fun getTypedConfigByName(
        typeName: String,
        userName: String,
        password: String = generateRandomString()
    ): TypedConfig {
            return when (typeName) {
                TypedConfig.OpenVpn.name -> TypedConfig.OpenVpn(userName)
                TypedConfig.WireGuard.name -> TypedConfig.WireGuard()
                TypedConfig.L2TP.name -> TypedConfig.L2TP(userName, password, generateRandomString())
                TypedConfig.PPTP.name -> TypedConfig.PPTP(userName, password)
                TypedConfig.Shadowsocks.name -> TypedConfig.Shadowsocks(password)
                else -> throw IllegalArgumentException()
            }
    }

    override suspend fun createServer(
        dropletId: Long,
        providerId: Long,
        logCallback: LogCallback?
    ): Boolean {
        logCallback?.onLog(LogStatus.CHECKING_STATUS)

        val server = serverDao.getDropletByIds(dropletId, providerId)

        if (server != null) {

            val agentResponse = agent.makeRequestAgent(
                ip = server.address,
                port = "80",
                aesKey = server.aesKey
            )

            if (agentResponse != null && agentResponse.status.code == "completed") {

                server.typedConfig.config = agentResponse.status.config
                serverDao.addTypedConfig(server.id, server.providerId, server.typedConfig)

                serverDao.updateStatus(
                    server.id,
                    server.providerId,
                    SshManager.Status.READY.toString()
                )
            } else {
                serverDao.updateStatus(
                    server.id,
                    server.providerId,
                    SshManager.Status.ERROR.toString()
                )

                throw BannedAddressException()
            }
        }

        return false
    }

    private fun saveCustomKey(keyPath: String): Long {

        val sshKeyId = getCurrentTimeMillis()

        keyDao.insert(sshKeyId = sshKeyId, privateKeyPath = keyPath)

        return sshKeyId
    }

    override suspend fun createCustomServer(
        typeName: String,
        userName: String,
        host: String,
        port: Int,
        password: String?,
        sshKey: String?,
        isV2RayEnabled: Boolean?,
        logCallback: LogCallback?
    ): Boolean {

        val privateKeyPath = sshKey?.let {
            sshManager.savePrivateKey(it).keyPath
        }

        val typedConfig = if (password != null) {
            getTypedConfigByName(typeName, userName, password)
        } else {
            getTypedConfigByName(typeName, userName)
        }

        val aesKey = cipher.generateKey(32)

        val script = getScript(getConnectionType(typedConfig), isV2RayEnabled, aesKey)

        if (setupCustomServer(userName, host, port, privateKeyPath, password, script, logCallback)) {

            val sshKeyId = privateKeyPath?.let {
                saveCustomKey(privateKeyPath)
            }

            val currentTime = getCurrentTimeMillis()
            serverDao.insert(
                id = currentTime,
                username = userName,
                providerId = Provider.Custom().getId(),
                name = "${typedConfig.getName()}-$userName",
                sshKeyId = sshKeyId,
                environmentStatus = SshManager.Status.PENDING.toString(),
                createdAt = currentTime.toString(),
                address = host,
                typedConfig = typedConfig,
                aesKey = aesKey
            )

            return true
        }

        throw BannedAddressException()
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
        isV2RayEnabled: Boolean?,
        logCallback: LogCallback?
    ): Boolean {

        logCallback?.onLog(LogStatus.SSH_KEYS)

        val token = getToken(providerId)

        val keyPair = createKeyPair()

        val providerApi = providerApiFactory.getProvider(providerId)
        val keyResponse = providerApi!!.importKey(token, "My VPN ssh key", keyPair.first.key)

        logCallback?.onLog(LogStatus.CREATING_SERVER)

        val userName = generateRandomString()
        val typedConfig = getTypedConfigByName(typeName, userName)
        val aesKey = cipher.generateKey(32)

        val createDropletResponse = providerApi.createDroplet(
            token = token,
            name = "vpn-${generateRandomString()}",
            regionSlug = regionSlug,
            sshKeyId = keyResponse.id,
            sshKey = keyPair.first.key,
            tag = DROPLET_TAG,
            script = getScript(
                getConnectionType(typedConfig),
                isV2RayEnabled,
                aesKey
            )
        )

        if (isDropletValid(createDropletResponse)) {

            val address = createDropletResponse.networks.first().ipAddress

            createDropletResponse.let { info ->
                serverDao.insert(
                    id = info.id,
                    username = userName,
                    providerId = providerId,
                    name = info.name,
                    sshKeyId = keyResponse.id,
                    serverStatus = info.status,
                    environmentStatus = SshManager.Status.STARTING.toString(),
                    createdAt = info.createdAt,
                    regionName = info.regionName,
                    address = address,
                    typedConfig = typedConfig,
                    aesKey = aesKey
                )
            }

            keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

            return true
        }

        throw IllegalArgumentException("Can't create valid droplet. Please try again")
    }

    private suspend fun setupServer(
        username: String,
        host: String,
        keyPathPrivate: String,
        connectionType: ConnectionType,
        logCallback: LogCallback? = null
    ): Boolean {

        return sshManager.setupServer(
            username,
            host,
            keyPathPrivate,
            connectionType,
            logCallback
        )
    }

    private suspend fun setupCustomServer(
        userName: String,
        host: String,
        port: Int,
        keyPathPrivate: String?,
        password: String?,
        script: String,
        logCallback: LogCallback? = null
    ): Boolean {

        return sshManager.setupCustomServer(
            userName,
            host,
            port,
            keyPathPrivate,
            password,
            script,
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

        try {
            providerApiFactory.getProvider(providerId)
                ?.deleteDroplet(getToken(providerId), dropletId)
        } catch (e: Exception) {}

        serverDao.deleteDroplet(dropletId, providerId)

        return true
    }

    override suspend fun getTypedConfig(server: ServerModel): TypedConfig {

        val keys = keyDao.selectById(server.sshKeyId!!)

        if (server.typedConfig.config.isNullOrEmpty()) {

            val file = sshManager.getConfigFile(
                server.username,
                server.address,
                keys?.privateKeyPath ?: throw NoDataException(),
                null,
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

        return provider!!.isServerAlive(token, dropletId)
    }

    private fun getScript(connectionType: ConnectionType, isV2RayEnable: Boolean?, encryptKey: String): String {
        val v2RayStr = isV2RayEnable?.let {
            "Environment=SHADOWSOCKS_PLUGIN_V2RAY_ENABLE=${if (isV2RayEnable ) '1' else '0'}\n"
        }  ?: ""

        return "#!/bin/sh\n" +
                "sudo wget -O /usr/local/bin/myvpn-agent https://github.com/my0419/myvpn-agent/releases/latest/download/myvpn-agent_linux_x86_64 &&\n" +
                "chmod +x /usr/local/bin/myvpn-agent &&\n" +
                "echo \"[Unit]\n" +
                "Description=MyVPN Agent\n" +
                "[Service]\n" +
                "ExecStart=/usr/local/bin/myvpn-agent\n" +
                "Restart=no\n" +
                "Environment=VPN_TYPE=$connectionType\n" +
                connectionType.let {
                    if (it is ConnectionType.L2TPType) {
                        "Environment=VPN_IPSEC_PSK=${it.key}\n"
                    } else {
                        ""
                    }
                } +

                connectionType.let {
                    try {
                        "Environment=VPN_USER=${connectionType.getUsername()}\n"
                    } catch (e: UnsupportedOperationException) {
                        ""
                    }
                } +

                connectionType.let {
                    try {
                        "Environment=VPN_PASSWORD=${connectionType.getPassword()}\n"
                    } catch (e: UnsupportedOperationException) {
                        ""
                    }
                } + v2RayStr +

                "Environment=ENCRYPT_KEY=${encryptKey}\n" +
                "Environment=VPN_CLIENT_CONFIG_FILE=/tmp/myvpn-client-config\n" +
                "[Install]\n" +
                "WantedBy=multi-user.target\" > /etc/systemd/system/myvpn-agent.service &&\n" +
                "systemctl start myvpn-agent.service"
    }

    companion object {

        private const val TAG = "ProviderRepository"

        val providers: List<Provider> = Provider.getAllServices()

    }
}