package com.merseyside.dropletapp.domain.interactor

import com.merseyside.dropletapp.connectionTypes.Type
import com.merseyside.dropletapp.domain.LockedType
import com.merseyside.kmpMerseyLib.domain.coroutines.CoroutineUseCase

class GetLockedTypesInteractor : CoroutineUseCase<List<LockedType>, Any>() {

    override suspend fun executeOnBackground(params: Any?): List<LockedType> {
        val types = Type.values().toList()

        val isSubscribed = isSubscribed()
        return types.map { type ->
            LockedType(type, !isSubscribed && type != Type.OPENVPN)
        }
    }

    private fun isSubscribed(): Boolean {
        return true
    }
}