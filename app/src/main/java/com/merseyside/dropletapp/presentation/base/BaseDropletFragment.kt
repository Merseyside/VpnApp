package com.merseyside.dropletapp.presentation.base

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.merseyside.mvvmcleanarch.presentation.fragment.BaseMvvmFragment
import com.merseyside.mvvmcleanarch.presentation.view.IFocusManager
import com.merseyside.mvvmcleanarch.presentation.view.OnBackPressedListener
import com.merseyside.mvvmcleanarch.utils.Logger

abstract class BaseDropletFragment<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseMvvmFragment<B, M>(), HasAppComponent, IFocusManager {

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
        val inputMethodManager = baseActivityView.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    protected fun goBack() {
        viewModel.goBack()
    }

    open fun hasTitleBackButton(): Boolean {
        if (fragmentManager != null) {
            return fragmentManager!!.backStackEntryCount > 0
        }

        return false
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
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "BaseDropletFragment"
    }
}