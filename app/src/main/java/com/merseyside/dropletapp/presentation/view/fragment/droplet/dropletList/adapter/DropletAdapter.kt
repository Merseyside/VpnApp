package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter

import android.util.Log
import androidx.appcompat.widget.PopupMenu
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.VpnApplication
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.merseyside.dropletapp.ssh.SshManager
import com.upstream.basemvvmimpl.presentation.adapter.BaseSortedAdapter
import com.upstream.basemvvmimpl.presentation.view.BaseViewHolder

class DropletAdapter : BaseSortedAdapter<Server, DropletItemViewModel>() {

    interface OnItemOptionsClickListener {
        fun onConnect(server: Server)

        fun onDelete(server: Server)

        fun onPrepare(server: Server)

        fun onShareOvpn(server: Server)
    }

    private var onShareClickListener: DropletItemViewModel.OnShareClickListener? = null
    private var onItemOptionsClickListener: OnItemOptionsClickListener? = null

    fun setOnShareClickListener(listener: DropletItemViewModel.OnShareClickListener) {
        onShareClickListener = listener
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.view_droplet
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    override fun createItemViewModel(obj: Server): DropletItemViewModel {
        return DropletItemViewModel(obj).apply { setOnShareClickListener(onShareClickListener) }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        setupPopup(holder)
    }

    private fun setupPopup(holder: BaseViewHolder) {

        holder.itemView.rootView.setOnLongClickListener {
            val item = getObjByPosition(holder.adapterPosition)

            val popup = PopupMenu(holder.itemView.context, holder.itemView.findViewById(R.id.status))
            popup.inflate(R.menu.menu_droplet_options)

            Log.d(TAG, item.environmentStatus.toString())

            if (item.connectStatus) {
                popup.menu.findItem(R.id.delete_action).isVisible = false
            }

            when (item.environmentStatus) {
                SshManager.Status.PENDING -> {
                    popup.menu.findItem(R.id.connect_action).isVisible = false
                    popup.menu.findItem(R.id.share_ovpn_action).isVisible = false
                }
                SshManager.Status.ERROR -> {
                    popup.menu.findItem(R.id.connect_action).isVisible = false
                    popup.menu.findItem(R.id.prepare_action).isVisible = false
                    popup.menu.findItem(R.id.share_ovpn_action).isVisible = false
                }
                SshManager.Status.IN_PROCESS -> {
                    popup.menu.findItem(R.id.prepare_action).isVisible = false
                    popup.menu.findItem(R.id.share_ovpn_action).isVisible = false
                }
                else -> {
                    popup.menu.findItem(R.id.prepare_action).isVisible = false

                    popup.menu.findItem(R.id.connect_action).title = if (item.connectStatus) {
                         VpnApplication.getInstance().getActualString(R.string.disconnect_action)
                    } else {
                        VpnApplication.getInstance().getActualString(R.string.connect_to_vpn_action)
                    }
                }
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

    companion object {
        private const val TAG = "DropletAdapter"
    }
}