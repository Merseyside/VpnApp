package com.merseyside.dropletapp.presentation.view.fragment.provider.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.providerApi.digitalOcean.entity.response.RegionPoint

class RegionAdapter(context: Activity,
                    private var resourceId: Int,
                    list: List<RegionPoint>
): ArrayAdapter<RegionPoint>(context, resourceId, list) {

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getRegionView(position, convertView, parent)
    }

    private fun getRegionView(position: Int, convertView: View?, parent: ViewGroup): View {
        val region = getItem(position)!!

        val view: View = convertView ?: inflater.inflate(resourceId, parent, false)

        val name: TextView = view.findViewById(R.id.name)

        name.text = region.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getRegionView(position, convertView, parent)
    }
}