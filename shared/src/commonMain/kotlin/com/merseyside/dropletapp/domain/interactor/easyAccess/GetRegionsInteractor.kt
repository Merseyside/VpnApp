package com.merseyside.dropletapp.domain.interactor.easyAccess

import com.merseyside.dropletapp.di.appComponent
import com.merseyside.dropletapp.di.easyAccessComponent
import com.merseyside.dropletapp.di.subscriptionComponent
import com.merseyside.dropletapp.domain.model.Region
import com.merseyside.dropletapp.domain.repository.EasyAccessRepository
import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.easyAccess.exception.InvalidTokenException
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import org.kodein.di.erased.instance

class GetRegionsInteractor : CoroutineUseCase<List<Region>, Any>() {

    private val repository: EasyAccessRepository by easyAccessComponent.instance()
    private val subscriptionManager: SubscriptionManager by appComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<Region> {

        val regionPoints = try {
             repository.getRegions()
        } catch (e: InvalidTokenException) {
            subscriptionManager.unsubscribe()
            throw e
        }

        val isSubscribed = subscriptionManager.isSubscribed()

        return regionPoints.mapIndexed { index, point ->
            Region(
                id = point.country.id,
                name = point.country.name,
                code = point.country.alpha2,
                connectionLevel = 1,
                isLocked = !isSubscribed && index != 0
            )
        }
    }

}