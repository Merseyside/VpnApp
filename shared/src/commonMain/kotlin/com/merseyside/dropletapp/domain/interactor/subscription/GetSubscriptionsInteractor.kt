package com.merseyside.dropletapp.domain.interactor.subscription

import com.merseyside.dropletapp.di.subscriptionComponent
import com.merseyside.dropletapp.domain.repository.SubscriptionRepository
import com.merseyside.dropletapp.subscriptions.entity.response.SubscriptionResponse
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase
import org.kodein.di.instance

class GetSubscriptionsInteractor : CoroutineUseCase<List<SubscriptionResponse>, Any>() {

    private val repository: SubscriptionRepository by subscriptionComponent.instance()

    override suspend fun executeOnBackground(params: Any?): List<SubscriptionResponse> {
        return repository.getSubscriptions()
    }

}