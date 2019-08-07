package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import com.merseyside.dropletapp.db.model.ServerModel
import com.upstream.basemvvmimpl.presentation.model.BaseComparableAdapterViewModel

class DropletItemViewModel(private var server: ServerModel) : BaseComparableAdapterViewModel<ServerModel>() {

    override fun isContentTheSame(obj: ServerModel): Boolean {
        return server == obj
    }

    override fun isItemsTheSame(obj: ServerModel): Boolean {
        return server == obj
    }

    override fun compareTo(obj: ServerModel): Int {
        return obj.id.compareTo(server.id)
    }

    override fun getItem(): ServerModel {
        return server
    }

    override fun setItem(item: ServerModel) {
        this.server = item
    }
}