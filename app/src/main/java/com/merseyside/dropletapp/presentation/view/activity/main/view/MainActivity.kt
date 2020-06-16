package com.merseyside.dropletapp.presentation.view.activity.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.ActivityMainBinding
import com.merseyside.dropletapp.presentation.base.BaseDropletActivity
import com.merseyside.dropletapp.presentation.di.component.DaggerMainComponent
import com.merseyside.dropletapp.presentation.di.module.MainModule
import com.merseyside.dropletapp.presentation.view.activity.main.model.MainViewModel
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.view.DropletListFragment
import com.merseyside.dropletapp.presentation.view.fragment.settings.view.SettingsFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import javax.inject.Inject


class MainActivity : BaseDropletActivity<ActivityMainBinding, MainViewModel>() {

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private var navigator : Navigator = object : SupportAppNavigator(this, R.id.container) {

        override fun applyCommand(command: Command?) {
            super.applyCommand(command)
            supportFragmentManager.executePendingTransactions()
        }

        override fun setupFragmentTransaction(
            command: Command?,
            currentFragment: Fragment?,
            nextFragment: Fragment?,
            fragmentTransaction: FragmentTransaction?
        ) {
            super.setupFragmentTransaction(command, currentFragment, nextFragment, fragmentTransaction)

            fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }

    override fun loadingObserver(isLoading: Boolean) {
    }

    override fun performInjection(bundle: Bundle?) {
        DaggerMainComponent.builder()
            .appComponent(getAppComponent())
            .mainModule(getAuthModule(bundle))
            .build().inject(this)
    }

    private fun getAuthModule(bundle: Bundle?): MainModule {
        return MainModule(this, bundle)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)


    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun getFragmentContainer(): Int? {
        return R.id.container
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                if (getCurrentFragment() !is SettingsFragment) {
                    viewModel.navigateToSettings()
                }
            }

            R.id.action_servers -> {
                if (getCurrentFragment() !is DropletListFragment) {
                    viewModel.navigateToDropletListScreen()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
