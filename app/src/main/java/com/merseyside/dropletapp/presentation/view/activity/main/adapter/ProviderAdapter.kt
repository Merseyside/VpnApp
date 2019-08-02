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
import com.merseyside.dropletapp.providerApi.Provider

class ProviderAdapter(
    context: Activity,
    private var resourceId: Int,
    list: List<Provider>
): ArrayAdapter<Provider>(context, resourceId, list) {

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getProviderView(position, convertView, parent)
    }

    private fun getProviderView(position: Int, convertView: View?, parent: ViewGroup): View {
        val provider = getItem(position)!!

        val view: View = convertView ?: inflater.inflate(resourceId, parent, false)

        val name: TextView = view.findViewById(R.id.name)
        val icon: ImageView = view.findViewById(R.id.icon)

        name.text = provider.getName()

        when(provider) {
            is Provider.DigitalOcean -> icon.setImageResource(R.drawable.digital_ocean)
            else -> {}
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getProviderView(position, convertView, parent)
    }
}