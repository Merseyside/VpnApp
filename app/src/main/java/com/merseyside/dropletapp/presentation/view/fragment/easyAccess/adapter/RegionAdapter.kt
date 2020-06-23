package com.merseyside.dropletapp.presentation.view.fragment.easyAccess.adapter

import android.content.Context
import com.merseyside.dropletapp.BR
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.presentation.view.fragment.easyAccess.model.RegionItemViewModel

class RegionAdapter(
    context: Context,
    regions: List<Region>
) : BaseSpinnerAdapter<Region, RegionItemViewModel>(context, R.layout.view_city, regions) {

    override fun createViewModel(obj: Region): RegionItemViewModel {
        return RegionItemViewModel(obj)
    }

    override fun getBindingVariable(): Int {
        return BR.obj
    }
}