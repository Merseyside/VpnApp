package com.merseyside.dropletapp.connectionTypes

import com.merseyside.dropletapp.domain.Server
import com.merseyside.kmpMerseyLib.domain.coroutines.applicationContext
import com.merseyside.kmpMerseyLib.utils.Logger
import com.merseyside.kmpMerseyLib.utils.time.getCurrentTimeMillis
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class ServiceConnectionType : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = applicationContext

    lateinit var config: String
    var server: Server? = null
        protected set

    protected var callback: ConnectionCallback? = null
    var timerJob: Job? = null

    protected var sessionTime = 0L

    fun setConnectionCallback(callback: ConnectionCallback?) {
        this.callback = callback
    }

    protected fun notifySessionStarts() {
        startTimer()
    }

    private fun startTimer() {
        if (timerJob == null) {
            timerJob = launch {

                sessionTime = getCurrentTimeMillis()

                fun getDelta(): Long {
                    return getCurrentTimeMillis() - sessionTime
                }

                while (isActive && server != null) {
                    val total = getDelta()

                    callback?.onSessionTime(total)

                    delay(1000)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.run {
            cancel()
        }

        timerJob = null
    }

    open fun start(server: Server) {
        if (VpnHelper.isPrepared()) {
            if (this.server != null) {
                if (this.server != server) throw IllegalArgumentException("Vpn has already running with another server.")
            } else {
                currentConnectionType = this
                this.server = server
            }
        } else {
            throw IllegalStateException("You have to prepare VPN before start it!")
        }
    }

    open fun stop() {
        server = null

        stopTimer()
        currentConnectionType = null

        callback?.onConnectionEvent(ConnectionLevel.IDLE)
    }

    companion object {
        private var currentConnectionType: ServiceConnectionType? = null

        fun getCurrentStatus(): ConnectionLevel {
            return if (isActive()) {
                ConnectionLevel.CONNECTED
            } else {
                ConnectionLevel.IDLE
            }
        }

        protected fun setCurrentConnectionType(connectionType: ServiceConnectionType?) {
            if (currentConnectionType != null) {
                currentConnectionType!!.stop()
            } else {
                currentConnectionType = connectionType
            }
        }

        fun turnOff() {
            setCurrentConnectionType(null)
        }

        fun isActive(): Boolean {
            return currentConnectionType != null
        }

        fun getCurrentConnectionType(): ServiceConnectionType? {
            return currentConnectionType
        }
    }
}