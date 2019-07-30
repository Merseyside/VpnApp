package com.merseyside.dropletapp.presentation.view.activity.auth.model

import android.os.Bundle
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import ru.terrakok.cicerone.Router

class MainViewModel(router: Router) : BaseDropletViewModel(router) {

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}