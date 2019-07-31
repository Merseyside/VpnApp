package com.merseyside.dropletapp.presentation.view.activity.main.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.entity.service.Service

class ServiceAdapter(
    context: Activity,
    private var resourceId: Int,
    list: List<Service>
): ArrayAdapter<Service>(context, resourceId, list) {

    val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getServiceView(position, convertView, parent)
    }

    private fun getServiceView(position: Int, convertView: View?, parent: ViewGroup): View {
        val service = getItem(position)!!

        val view: View

        if (convertView != null) {
            view = convertView
        } else {
            view = inflater.inflate(resourceId, parent, false)
        }

        val name: TextView = view.findViewById(R.id.name)
        val icon: ImageView = view.findViewById(R.id.icon)

        name.text = service.getName()

        when(service) {
            is Service.DigitalOceanService -> icon.setImageResource(R.drawable.digital_ocean)
            else -> {}
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getServiceView(position, convertView, parent)
    }
}