package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.adapter

import androidx.appcompat.widget.PopupMenu
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model.DropletItemViewModel
import com.merseyside.merseyLib.adapters.BaseSortedAdapter
import com.merseyside.merseyLib.view.BaseBindingHolder

class DropletAdapter : BaseSortedAdapter<Server, DropletItemViewModel>() {

    interface OnItemOptionsClickListener {

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

    override fun onBindViewHolder(holder: BaseBindingHolder<DropletItemViewModel>, position: Int) {
        super.onBindViewHolder(holder, position)

        setupPopup(holder)
    }

    private fun setupPopup(holder: BaseBindingHolder<DropletItemViewModel>) {

        holder.itemView.rootView.setOnLongClickListener {
            val item = getItemByPosition(holder.adapterPosition)

            val popup = PopupMenu(holder.itemView.context, holder.itemView.findViewById(R.id.type))
            popup.inflate(R.menu.menu_droplet_options)

            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_action -> {
                        onItemOptionsClickListener?.onDelete(item)
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