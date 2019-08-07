package com.merseyside.dropletapp.presentation.navigation

import androidx.fragment.app.Fragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view.AddDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view.DropletListFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.view.TokenFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class Screens {

    class TokenScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return TokenFragment.newInstance()
        }
    }

    class AddDropletScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return AddDropletFragment.newInstance()
        }
    }

    class DropletListScreen : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return DropletListFragment.newInstance()
        }
    }
}