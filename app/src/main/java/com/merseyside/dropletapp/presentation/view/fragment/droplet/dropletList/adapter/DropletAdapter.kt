package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter

import androidx.appcompat.widget.PopupMenu
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.adapter.BaseAdapter
import com.upstream.basemvvmimpl.presentation.adapter.BaseSortedAdapter
import com.upstream.basemvvmimpl.presentation.view.BaseViewHolder

class DropletAdapter : BaseSortedAdapter<Server, DropletItemViewModel>() {

    interface OnItemOptionsClickListener {
        fun onConnect(server: Server)

        fun onDelete(server: Server)

        fun onPrepare(server: Server)

        fun onShareOvpn(server: Server)
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

        val item = getObjByPosition(position)

        holder.itemView.rootView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.itemView.findViewById(R.id.status))
            popup.inflate(R.menu.menu_droplet_options)

            if (item.environmentStatus == SshManager.Status.PENDING) {
                popup.menu.findItem(R.id.connect_action).isVisible = false
            } else {
                popup.menu.findItem(R.id.prepare_action).isVisible = false
            }

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_action -> {
                        onItemOptionsClickListener?.onDelete(item)
                    }

                    R.id.connect_action -> {
                        onItemOptionsClickListener?.onConnect(item)
                    }

                    R.id.prepare_action -> {
                        onItemOptionsClickListener?.onPrepare(item)
                    }

                    R.id.share_ovpn_action -> {
                        onItemOptionsClickListener?.onShareOvpn(item)
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