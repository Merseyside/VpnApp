package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.model

import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.dropletapp.data.entity.TypedConfig
import com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter.TypesAdapter
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseAdapter
import com.merseyside.mvvmcleanarch.presentation.adapter.BaseSelectableAdapter

@BindingAdapter("bind:types")
fun setTypes(recyclerView: RecyclerView, types: List<String>?) {
    if (types != null) {
        val adapter = recyclerView.adapter?.let { it as TypesAdapter } ?: TypesAdapter().also {
            recyclerView.adapter = it
        }

        adapter.add(types)
    }
}


@BindingAdapter("bind:onTypeClick")
fun setSelectedMembers(view: RecyclerView, typedConfig: String?) {}

@BindingAdapter(value = ["onTypeClickAttrChanged"]) // AttrChanged required postfix
fun setSelectedMembersListener(recyclerView: RecyclerView, listener: InverseBindingListener?) {
    if (listener != null) {
        val adapter = recyclerView.adapter?.let { it as TypesAdapter } ?: TypesAdapter().also {
            recyclerView.adapter = it
        }

        adapter.setOnItemSelectedListener(object : BaseSelectableAdapter.OnItemSelectedListener<String> {
            override fun onSelected(isSelected: Boolean, item: String) {
                if (isSelected) {
                    listener.onChange()
                }
            }
        })
    }
}

@InverseBindingAdapter(attribute = "bind:onTypeClick")
fun getSelectedMembers(view: RecyclerView): String? {
    return view.adapter?.let {
        it as TypesAdapter

        it.getSelectedItem()
    }
}

@BindingAdapter("bind:backgroundDrawable")
fun setDrawableBackground(view: View, @DrawableRes res: Int?) {
    if (res != null) {
        view.background = ContextCompat.getDrawable(view.context, res)
    }
}