package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.merseyside.merseyLib.adapters.ItemPositionInterface
import com.merseyside.merseyLib.model.BaseAdapterViewModel
import com.merseyside.merseyLib.view.BaseBindingHolder

abstract class BaseSpinnerAdapter<M, T : BaseAdapterViewModel<M>>(
    context: Context,
    @LayoutRes private val itemLayoutResourceId: Int,
    list: List<M>
) : BaseAdapter(), ItemPositionInterface<BaseAdapterViewModel<M>> {

    private lateinit var list: List<T>
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        add(list)
    }

    abstract fun createViewModel(obj: M): T

    abstract fun getBindingVariable(): Int

    private fun add(list: List<M>) {
        this.list = list.map { obj ->
            createViewModel(obj).apply {
                //setItemPositionInterface(this)
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: BaseBindingHolder<T>

        if (convertView == null) {
            val itemBinding: ViewDataBinding = DataBindingUtil.inflate(
                inflater,
                itemLayoutResourceId,
                parent,
                false
            )

            holder = BaseBindingHolder(itemBinding)
            itemBinding.root.tag = holder
            //itemBinding.executePendingBindings()
        } else {
            holder = convertView.tag as BaseBindingHolder<T>
        }

        holder.binding.setVariable(getBindingVariable(), getItem(position))
        return holder.binding.root
    }

    override fun getItem(position: Int): T {
        return list[position]
    }

    fun getModel(position: Int): M {
        return getItem(position).obj
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    @Throws(IllegalArgumentException::class)
    open fun getPositionOfModel(model: T): Int {
        list.forEachIndexed { index, t ->
            if (t == model) return index
        }

        throw IllegalArgumentException("No data found")
    }

    override fun getPosition(model: BaseAdapterViewModel<M>): Int {
        return getPositionOfModel(model as T)
    }

    override fun isLast(model: BaseAdapterViewModel<M>): Boolean {
        return getPosition(model) == count - 1
    }

    override fun isFirst(model: BaseAdapterViewModel<M>): Boolean {
        return getPosition(model) == 0
    }
}