package com.merseyside.dropletapp.presentation.base

import com.merseyside.dropletapp.VpnApplication

interface HasAppComponent {

    fun getAppComponent() = VpnApplication.getInstance().appComponent
}