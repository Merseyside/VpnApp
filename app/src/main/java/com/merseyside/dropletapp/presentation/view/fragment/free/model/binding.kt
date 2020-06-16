package com.merseyside.dropletapp.presentation.view.fragment.free.model

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.dropletapp.presentation.view.fragment.free.adapter.TypeAdapter
import com.merseyside.merseyLib.adapters.BaseSelectableAdapter

@BindingAdapter("onTypeSelected")
fun setSelectedListener(
    view: RecyclerView,
    oldListener: BaseSelectableAdapter.OnItemSelectedListener<Any>?,
    newListener: BaseSelectableAdapter.OnItemSelectedListener<Any>?
) {
    val adapter = view.adapter ?: initRecyclerView(view)

    adapter as BaseSelectableAdapter<Any, *>

    if (newListener != null) {
        adapter.setOnItemSelectedListener(newListener)
    }
}

@BindingAdapter("types")
fun setTypes(view: RecyclerView, types: List<LockedType>?) {
    if (types != null) {
        val adapter = view.adapter ?: initRecyclerView(view)

        adapter as TypeAdapter

        adapter.add(types)
    }
}

private fun initRecyclerView(recyclerView: RecyclerView): TypeAdapter {
    return TypeAdapter().also {
        recyclerView.adapter = it
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.HORIZONTAL)
    }
}

