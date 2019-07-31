package com.merseyside.dropletapp.presentation.view.fragment.token.model

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.merseyside.dropletapp.data.entity.Token
import com.merseyside.dropletapp.data.entity.service.Service
import com.merseyside.dropletapp.domain.interactor.GetServicesInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import ru.terrakok.cicerone.Router

class TokenViewModel(
    router: Router,
    private val getServicesUseCase: GetServicesInteractor
) : BaseDropletViewModel(router) {

    val tokenObservableField = ObservableField<Token>()
    val tokenNameObservableField = ObservableField<String>()

    val serviceLiveData = MutableLiveData<List<Service>>()

    init {
        getServices()
    }

    override fun dispose() {
    }

    override fun readFrom(bundle: Bundle) {
    }

    override fun writeTo(bundle: Bundle) {
    }

    private fun getServices() {
        getServicesUseCase.execute(
            onComplete = {
                serviceLiveData.value = it
            },
            onError = {

            }
        )

    }

    fun saveToken(service: Service) {

    }
}