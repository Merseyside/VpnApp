package com.merseyside.dropletapp.presentation.base

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.view.AddDropletFragment
import com.merseyside.dropletapp.presentation.view.fragment.droplet.droplet.view.DropletFragment
import com.merseyside.merseyLib.presentation.fragment.BaseVMFragment
import com.merseyside.merseyLib.presentation.view.IFocusManager

abstract class BaseDropletFragment<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseVMFragment<B, M>(), HasAppComponent, IFocusManager {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keepOneFocusedView()
    }

    override fun onStart() {
        super.onStart()

        setTitleBackButtonEnabled()
    }

    override fun getRootView(): View {
        return view!!
    }

    protected fun closeKeyboard() {
        val inputMethodManager = baseActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    protected fun goBack() {
        if (parentFragmentManager.backStackEntryCount > 0) viewModel.goBack()
    }

    open fun hasTitleBackButton(): Boolean {
        return parentFragmentManager.backStackEntryCount > 0
    }

    private fun setTitleBackButtonEnabled() {
        if (getActionBar() != null) {
            getActionBar()!!.setDisplayHomeAsUpEnabled(hasTitleBackButton())

            if (hasTitleBackButton()) {
                setHasOptionsMenu(true)
            }
        }
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            goBack()
        } else {
            return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.action_settings).isVisible = this !is AddDropletFragment && this !is DropletFragment
        menu.findItem(R.id.action_servers).isVisible = this !is AddDropletFragment && this !is DropletFragment
    }
}