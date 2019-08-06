package com.merseyside.dropletapp.presentation.view.fragment.droplet.addDroplet.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.data.db.token.TokenEntity

class TokenAdapter(
    context: Activity,
    private var resourceId: Int,
    list: List<TokenEntity>
): ArrayAdapter<TokenEntity>(context, resourceId, list) {

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getTokenView(position, convertView, parent)
    }

    private fun getTokenView(position: Int, convertView: View?, parent: ViewGroup): View {
        val token = getItem(position)!!

        val view: View = convertView ?: inflater.inflate(resourceId, parent, false)

        val name: TextView = view.findViewById(R.id.name)

        name.text = token.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getTokenView(position, convertView, parent)
    }
}