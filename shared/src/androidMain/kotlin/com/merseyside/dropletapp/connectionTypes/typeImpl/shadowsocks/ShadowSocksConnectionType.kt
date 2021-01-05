package com.merseyside.dropletapp.connectionTypes.typeImpl.shadowsocks

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.merseyside.dropletapp.connectionTypes.AndroidImpl
import com.merseyside.dropletapp.connectionTypes.ConnectionLevel
import com.merseyside.dropletapp.connectionTypes.ServiceConnectionType
import com.merseyside.dropletapp.domain.Server
import com.merseyside.kmpMerseyLib.utils.Logger
import com.merseyside.kmpMerseyLib.utils.ext.log
import java.io.BufferedInputStream
import java.io.InputStream

actual class ShadowSocksConnectionType actual constructor() :
    ServiceConnectionType(), ShadowsocksConnection.Callback, AndroidImpl {

    override var context: Context? = null
    private var profile: Profile? = null

    override fun start(server: Server) {
        super.start(server)

        Core.init(context!! as Application, context!!.javaClass.kotlin)
        profile = prepareProfile()

        if (profile != null) {
            ProfileManager.createProfile(profile!!)
            Core.switchProfile(profile!!.id)

            Core.startService()
            notifySessionStarts()
        }

        callback?.onConnectionEvent(ConnectionLevel.CONNECTED)
    }

    override fun stop() {
        super.stop()

        Core.stopService()
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        val level = when(state) {
            BaseService.State.Connecting -> ConnectionLevel.CONNECTING
            BaseService.State.Connected -> ConnectionLevel.CONNECTED
            else -> ConnectionLevel.IDLE
        }

        callback?.onConnectionEvent(level)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        stateChanged(BaseService.State.values()[service.state], null, null)
    }

    override fun onServiceDisconnected() {
        super.onServiceDisconnected()

        callback?.onConnectionEvent(ConnectionLevel.IDLE)
    }

    @Throws(IllegalArgumentException::class)
    private fun prepareProfile(): Profile {
        Logger.log(this, config)
        return ConfigDataMapper.transform(config)
    }
}