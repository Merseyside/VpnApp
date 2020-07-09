package com.merseyside.dropletapp.presentation.base

import androidx.databinding.ViewDataBinding
import com.merseyside.archy.presentation.activity.BaseVMActivity
import com.merseyside.dropletapp.R

abstract class BaseDropletActivity<B : ViewDataBinding, M : BaseDropletViewModel>
    : BaseVMActivity<B, M>(), HasAppComponent {

    override fun getLanguage(): String {
        val lang = super.getLanguage()

        val languages = resources.getStringArray(R.array.language_entry_values)

        return if (languages.contains(lang)) lang
        else return "en"
    }

}