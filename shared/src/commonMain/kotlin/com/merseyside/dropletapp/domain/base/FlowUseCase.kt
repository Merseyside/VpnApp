package com.merseyside.chatapp.domain.base

import com.merseyside.dropletapp.domain.base.applicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

@FlowPreview
abstract class FlowUseCase<T, Params> : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var backgroundContext: CoroutineContext = applicationContext

    @ExperimentalCoroutinesApi
    protected abstract fun executeOnBackground(params: Params?): Flow<T>

    fun observe(params: Params?): Flow<T> {

        return executeOnBackground(params)
                .flowOn(backgroundContext)

    }
}