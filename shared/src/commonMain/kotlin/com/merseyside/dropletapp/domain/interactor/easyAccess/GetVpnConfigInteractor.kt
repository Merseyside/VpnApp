package com.merseyside.dropletapp.domain.interactor.easyAccess

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.di.appComponent
import com.merseyside.dropletapp.di.easyAccessComponent
import com.merseyside.dropletapp.di.subscriptionComponent
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.easyAccess.exception.InvalidTokenException
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.SettingsHelper
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import org.kodein.di.erased.instance

class GetVpnConfigInteractor : CoroutineUseCase<String, GetVpnConfigInteractor.Params>() {

    private val repository: EasyAccessRepository by easyAccessComponent.instance()
    private val settingsHelper: SettingsHelper by appComponent.instance()
    private val subscriptionManager: SubscriptionManager by appComponent.instance()

    data class Params(
        val type: Type,
        val locale: String,
        val region: Region
    )

    override suspend fun executeOnBackground(params: Params?): String {
        settingsHelper.setLocale(params!!.locale)

        return try {
            if (subscriptionManager.isSubscribed()) {

                repository.getVpnConfig(params.type, params.region.id)
            } else {
                repository.getFreeVpnConfig(params.type, params.region.id)
            }
        } catch (e: InvalidTokenException) {
            subscriptionManager.unsubscribe()

            throw e
        }
    }
}