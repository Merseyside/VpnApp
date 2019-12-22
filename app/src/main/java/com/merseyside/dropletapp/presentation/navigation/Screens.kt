package com.merseyside.dropletapp.presentation.navigation

import androidx.fragment.app.Fragment
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.model.OAuthProvider
import com.merseyside.dropletapp.presentation.view.fragment.authFragment.view.AuthFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view.AddDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view.DropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view.DropletListFragment
import com.merseyside.dropletapp.presentation.view.fragment.qr.QrFragment
import com.merseyside.dropletapp.presentation.view.fragment.settings.view.SettingsFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.view.TokenFragment
import com.merseyside.dropletapp.providerApi.Provider
import ru.terrakok.cicerone.android.support.SupportAppScreen

class Screens {

    class TokenScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return TokenFragment.newInstance()
        }
    }

    class AddDropletScreen(private val provider: Provider) : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return AddDropletFragment.newInstance(provider)
        }
    }

    class DropletListScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return DropletListFragment.newInstance()
        }
    }

    class SettingsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SettingsFragment.newInstance()
        }
    }

    class AuthScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return AuthFragment.newInstance()
        }
    }

    class DropletScreen(private val server: Server) : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return DropletFragment.newInstance(server)
        }
    }

    class QrScreen(private val config: String) : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return QrFragment.newInstance(config)
        }
    }
}