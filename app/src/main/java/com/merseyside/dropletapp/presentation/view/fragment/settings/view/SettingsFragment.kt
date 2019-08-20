package com.merseyside.dropletapp.presentation.view.fragment.settings.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.dropletapp.R
import com.upstream.basemvvmimpl.presentation.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    override fun setLayoutId(): Int {
        return R.layout.fragment_settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        language.currentEntryValue = getLanguage()
        language.setOnValueChangeListener {
            updateLanguage(it)
        }
    }

    override fun updateLanguage(context: Context) {
        language.updateLanguage(context)
    }

    override fun getTitle(context: Context): String? {
        return null
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}