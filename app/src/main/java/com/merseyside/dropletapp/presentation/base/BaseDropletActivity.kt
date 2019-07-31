package com.merseyside.dropletapp.presentation.base

import androidx.databinding.ViewDataBinding
import com.upstream.basemvvmimpl.presentation.activity.BaseMvvmActivity

abstract class BaseDropletActivity<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseMvvmActivity<B, M>(), HasAppComponent {

//    override fun getRootView(): View {
//        return window.decorView.findViewById(android.R.id.content)
//    }

    companion object {
        private const val TAG = "BaseDropletActivity"
    }

}