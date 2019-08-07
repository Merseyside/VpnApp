package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter

import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.upstream.basemvvmimpl.presentation.adapter.BaseSortedAdapter

class DropletAdapter : BaseSortedAdapter<ServerModel, DropletItemViewModel>() {

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_droplet
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: ServerModel): DropletItemViewModel {
        return DropletItemViewModel(obj)
    }
}