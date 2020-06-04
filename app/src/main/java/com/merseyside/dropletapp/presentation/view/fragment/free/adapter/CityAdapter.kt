package com.merseyside.dropletapp.presentation.view.fragment.free.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.presentation.view.fragment.free.model.City
import com.merseyside.dropletapp.presentation.view.fragment.free.model.CityItemViewModel
import com.merseyside.merseyLib.adapters.BaseAdapter

class CityAdapter(
    context: Context
) : BaseSpinnerAdapter<City, CityItemViewModel>(context, R.layout.view_city, populateCities()) {

    override fun createViewModel(obj: City): CityItemViewModel {
        return CityItemViewModel(obj)
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }

    companion object {
        private fun populateCities(): List<City> {
            return listOf(
                City(
                    name = "Amsterdam 3",
                    country = "hl",
                    connectionLevel = 3,
                    isLocked = false
                ),

                City(
                    name = "Bangalore",
                    country = "in",
                    connectionLevel = 3,
                    isLocked = true
                ),

                City(
                    name = "Frankfurt",
                    country = "de",
                    connectionLevel = 1,
                    isLocked = true
                ),

                City(
                    name = "London",
                    country = "uk",
                    connectionLevel = 2,
                    isLocked = true
                ),

                City(
                    name = "New York 1",
                    country = "us",
                    connectionLevel = 3,
                    isLocked = true
                ),

                City(
                    name = "San Francisko 2",
                    country = "us",
                    connectionLevel = 2,
                    isLocked = true
                ),

                City(
                    name = "Singapore",
                    country = "sg",
                    connectionLevel = 3,
                    isLocked = true
                ),

                City(
                    name = "Toronto",
                    country = "ca",
                    connectionLevel = 1,
                    isLocked = true
                )
            )
        }
    }
}