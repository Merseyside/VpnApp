package com.merseyside.dropletapp.presentation.navigation

import androidx.fragment.app.Fragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view.AddDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.view.TokenFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class Screens {

    class TokenScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return TokenFragment.newInstance()
        }
    }

    class ProviderScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return AddDropletFragment.newInstance()
        }
    }
}