package com.merseyside.dropletapp.presentation.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.merseyside.dropletapp.VpnApplication
import com.upstream.basemvvmimpl.presentation.fragment.BaseMvvmFragment

abstract class BaseDropletFragment<B : ViewDataBinding, M : BaseDropletViewModel> : BaseMvvmFragment<B, M>(), HasAppComponent {

    override fun getApplicationContext(): Context {
        return VpnApplication.getInstance()
    }

    override fun clear() {
    }
}