package com.merseyside.dropletapp.presentation.view.activity.main.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.databinding.ActivityMainBinding
import com.merseyside.dropletapp.presentation.base.BaseDropletActivity
import com.merseyside.dropletapp.presentation.di.component.DaggerMainComponent
import com.merseyside.dropletapp.presentation.di.module.MainModule
import com.merseyside.dropletapp.presentation.view.activity.main.model.MainViewModel
import com.merseyside.dropletapp.presentation.view.fragment.provider.view.ProviderFragment
import com.merseyside.dropletapp.presentation.view.fragment.token.view.TokenFragment
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

    override fun clear() {
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

    override fun setBindingVariable(): Int {
        return BR.viewModel
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        doLayout()

        if (savedInstance == null) {
            init()
        }
    }

    private fun init() {
        viewModel.newRootScreen()
    }

    private fun doLayout() {
        fixBottomNavigation()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }

    }

    private fun fixBottomNavigation() {
        val menu = (binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView)

        for (i in 0 until menu.childCount) {
            val item = menu.getChildAt(i) as BottomNavigationItemView

            for (j in 0 until item.childCount) {
                val child = item.getChildAt(j) as View

                child.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

                child.findViewById<View?>(com.google.android.material.R.id.largeLabel)
                    ?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))

                child.findViewById<View?>(com.google.android.material.R.id.smallLabel)
                    ?.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            }
        }
    }

    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val currentFragment = getCurrentFragment()

        when(menuItem.itemId) {
            R.id.nav_tokens ->
                if (currentFragment !is TokenFragment) {
                    viewModel.navigateToTokenScreen()
                }

            R.id.nav_providers ->
                if (currentFragment !is ProviderFragment) {
                    viewModel.navigateToProviderScreen()
                }

            else -> {

            }
        }

        return true
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

    companion object {
        private const val TAG = "MainActivity"
    }
}
