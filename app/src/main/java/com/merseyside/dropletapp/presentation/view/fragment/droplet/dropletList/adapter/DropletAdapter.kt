package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter

import androidx.appcompat.widget.PopupMenu
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.upstream.basemvvmimpl.presentation.adapter.BaseSortedAdapter
import com.upstream.basemvvmimpl.presentation.view.BaseViewHolder

class DropletAdapter : BaseSortedAdapter<Server, DropletItemViewModel>() {

    interface OnItemOptionsClickListener {
        fun onConnect(server: Server)

        fun onDelete(server: Server)
    }

    private var onItemOptionsClickListener: OnItemOptionsClickListener? = null

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_droplet
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: Server): DropletItemViewModel {
        return DropletItemViewModel(obj)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.itemView.rootView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.itemView.findViewById(R.id.status))
            popup.inflate(R.menu.menu_droplet_options)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_action -> {
                        onItemOptionsClickListener?.onDelete(getObjForPosition(position).getItem())
                    }

                    R.id.connect_action -> {
                        onItemOptionsClickListener?.onConnect(getObjForPosition(position).getItem())
                    }

                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }

                true

            }
            popup.show()

            true
        }
    }

    fun setOnItemOptionClickListener(onItemOptionsClickListener: OnItemOptionsClickListener?) {
        this.onItemOptionsClickListener = onItemOptionsClickListener
    }
}