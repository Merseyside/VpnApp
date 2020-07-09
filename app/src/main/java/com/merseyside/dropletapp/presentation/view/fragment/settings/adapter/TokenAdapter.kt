package com.merseyside.dropletapp.presentation.view.fragment.settings.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity
import com.merseyside.dropletapp.presentation.view.fragment.settings.model.TokenItemViewModel
import com.merseyside.adapters.base.BaseAdapter
import com.merseyside.adapters.view.BaseBindingHolder

class TokenAdapter : BaseAdapter<TokenEntity, TokenItemViewModel>() {

    override fun createItemViewModel(obj: TokenEntity): TokenItemViewModel {
        return TokenItemViewModel(obj)
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return if (position % 2 == 0) {
            R.layout.view_token
        } else {
            R.layout.view_token_odd
        }
    }
}