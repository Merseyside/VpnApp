package com.merseyside.dropletapp.presentation.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.upstream.basemvvmimpl.presentation.fragment.BaseMvvmFragment
import com.upstream.basemvvmimpl.presentation.view.IFocusManager

abstract class BaseDropletFragment<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseMvvmFragment<B, M>(), HasAppComponent, IFocusManager {

    override fun clear() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keepOneFocusedView()
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

    companion object {
        private const val TAG = "BaseDropletFragment"
    }
}