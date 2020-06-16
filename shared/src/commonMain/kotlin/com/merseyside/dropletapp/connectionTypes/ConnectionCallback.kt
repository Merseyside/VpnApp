package com.merseyside.dropletapp.connectionTypes

interface ConnectionCallback {

    fun onConnectionEvent(connectionLevel: ConnectionLevel)

    fun onSessionTime(timestamp: Long)
}