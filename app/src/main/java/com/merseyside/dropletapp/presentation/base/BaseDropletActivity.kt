package com.merseyside.dropletapp.presentation.base

import androidx.databinding.ViewDataBinding
import com.merseyside.dropletapp.R
import com.merseyside.merseyLib.presentation.activity.BaseVMActivity

abstract class BaseDropletActivity<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseVMActivity<B, M>(), HasAppComponent {

//    override fun getRootView(): View {
//        return window.decorView.findViewById(android.R.id.content)
//    }

    companion object {
        private const val TAG = "BaseDropletActivity"
    }

    override fun getLanguage(): String {
        val lang = super.getLanguage()

        val languages = resources.getStringArray(R.array.language_entry_values)

        return if (languages.contains(lang)) lang
        else return "en"
    }

}