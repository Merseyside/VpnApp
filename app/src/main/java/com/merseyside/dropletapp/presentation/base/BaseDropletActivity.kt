package com.merseyside.dropletapp.presentation.base

import androidx.databinding.ViewDataBinding
import com.merseyside.dropletapp.R
import com.merseyside.merseyLib.presentation.activity.BaseVMActivity

abstract class BaseDropletActivity<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseVMActivity<B, M>(), HasAppComponent {

    override fun getLanguage(): String {
        val lang = super.getLanguage()

        val languages = resources.getStringArray(R.array.language_entry_values)

        return if (languages.contains(lang)) lang
        else return "en"
    }

}