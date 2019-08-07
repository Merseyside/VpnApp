package com.merseyside.dropletapp.presentation.view.activity.main.model

import android.os.Bundle
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import ru.terrakok.cicerone.Router

class MainViewModel(private val router: Router) : BaseDropletViewModel(router) {

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
    }

    fun newRootScreen() {
        router.newRootScreen(Screens.TokenScreen())
    }

    fun navigateToTokenScreen() {
        router.newRootScreen(Screens.TokenScreen())
    }

    fun navigateToDropletListScreen() {
        router.newRootScreen(Screens.DropletListScreen())
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}