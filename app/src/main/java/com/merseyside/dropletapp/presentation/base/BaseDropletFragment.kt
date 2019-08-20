package com.merseyside.dropletapp.presentation.base

import android.os.Bundle
import android.view.View
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

    companion object {
        private const val TAG = "BaseDropletFragment"
    }
}