package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter.RegionAdapter
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter.TypeAdapter
import com.merseyside.merseyLib.adapters.BaseSelectableAdapter

@BindingAdapter("onTypeSelected")
fun setSelectedListener(
    view: RecyclerView,
    oldListener: BaseSelectableAdapter.OnItemSelectedListener<Any>?,
    newListener: BaseSelectableAdapter.OnItemSelectedListener<Any>?
) {
    val adapter = view.adapter ?: initTypeRecyclerView(view)

    adapter as BaseSelectableAdapter<Any, *>

    if (newListener != null) {
        adapter.setOnItemSelectedListener(newListener)
    }
}


@BindingAdapter("types")
fun setTypes(view: RecyclerView, types: List<LockedType>?) {
    if (types != null) {
        val adapter = view.adapter ?: initTypeRecyclerView(view)

        adapter as TypeAdapter

        adapter.add(types)
    }
}

private fun initTypeRecyclerView(recyclerView: RecyclerView): TypeAdapter {
    return TypeAdapter().also {
        recyclerView.adapter = it
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.HORIZONTAL)
    }
}


@BindingAdapter("app:onRegionSelected")
fun setSelectedRegion(view: Spinner, region: Region?) {}

@BindingAdapter(value = ["onRegionSelectedAttrChanged"]) // AttrChanged required postfix
fun setSelectedRegionListener(spinner: Spinner, listener: InverseBindingListener?) {
    if (listener != null) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listener.onChange()
            }

        }
    }
}

@InverseBindingAdapter(attribute = "app:onRegionSelected")
fun getSelectedRegion(spinner: Spinner): Region? {
    val adapter = spinner.adapter as RegionAdapter

    return adapter.getModel(spinner.selectedItemPosition)
}

//private fun getRegionAdapter(spinner: Spinner, regions: List<Region>? = null): RegionAdapter {
//    return if (spinner.adapter == null) {
//        if (regions != null) {
//            RegionAdapter(spinner.context, regions).also {
//                spinner.adapter = it
//            }
//        } else throw IllegalStateException()
//    } else {
//        spinner.adapter as RegionAdapter
//    }
//}

