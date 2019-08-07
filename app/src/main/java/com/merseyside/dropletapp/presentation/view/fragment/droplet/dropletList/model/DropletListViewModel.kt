package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.db.model.ServerModel
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import ru.terrakok.cicerone.Router

class DropletListViewModel(private val router: Router) : BaseDropletViewModel(router) {

    val dropletsVisibility = ObservableField<Boolean>()

    val dropletLiveData = MutableLiveData<List<ServerModel>>()


    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    override fun dispose() {
    }

    fun loadServers() {

    }

    fun navigateToAddDropletScreen() {
        router.navigateTo(Screens.AddDropletScreen())
    }


}