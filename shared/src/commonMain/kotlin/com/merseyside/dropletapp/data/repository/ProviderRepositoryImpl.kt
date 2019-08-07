package com.merseyside.dropletapp.data.repository

import com.merseyside.dropletapp.ssh.SshManager
import com.merseyside.dropletapp.data.db.key.KeyDao
import com.merseyside.dropletapp.data.db.server.ServerDao
import com.merseyside.dropletapp.data.entity.PrivateKey
import com.merseyside.dropletapp.data.entity.PublicKey
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.providerApi.Provider
import com.merseyside.dropletapp.domain.repository.ProviderRepository
import com.merseyside.dropletapp.providerApi.ProviderApiFactory
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint
import com.merseyside.dropletapp.utils.DROPLET_TAG
import com.merseyside.dropletapp.utils.isDropletValid

class ProviderRepositoryImpl(
    private val providerApiFactory: ProviderApiFactory,
    private val sshManager: SshManager,
    private val keyDao: KeyDao,
    private val serverDao: ServerDao
) : ProviderRepository {

    override suspend fun getServices(): List<Provider> {
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

        keyDao.insert(keyResponse.id, keyPair.first.keyPath, keyPair.second.keyPath, token)

        val createDropletResponse = provider.createDroplet(
            token = token,
            name = serverName,
            regionSlug = regionSlug,
            sshKeyId = keyResponse.id,
            tag = DROPLET_TAG
        )

        if (createDropletResponse.id <= 0L) throw IllegalStateException("Error while creating droplet")

        val infoResponse = provider.getDropletInfo(token, createDropletResponse.id)

        if (isDropletValid(infoResponse)) {
            infoResponse.let {
                serverDao.insert(
                    id = it.id,
                    name = it.name,
                    status = it.status,
                    createdAt = it.createdAt,
                    networks = it.networks
                )
            }

            return sshManager.openSshConnection(
                "root",
                infoResponse.networks.first().ipAddress,
                keyPair.second.keyPath
            )
        } else throw IllegalArgumentException("Droplet is not valid")

    }

    companion object {

        private var providers: List<Provider> = Provider.getAllServices()

    }
}