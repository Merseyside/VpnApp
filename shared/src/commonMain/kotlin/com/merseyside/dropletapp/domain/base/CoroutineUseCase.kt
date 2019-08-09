package com.merseyside.dropletapp.domain.base

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineUseCase<T, Params> : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var backgroundContext: CoroutineContext = networkContext

    protected abstract suspend fun executeOnBackground(params: Params?): T

    fun execute(
        onComplete: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        params: Params? = null,
        showProgress: () -> Unit = {},
        hideProgress: () -> Unit = {}
    ) {
        launch {
            try {
                showProgress()
                val result = withContext(backgroundContext) {
                    executeOnBackground(params)
                }
                onComplete.invoke(result)
                hideProgress()
            } catch (e: CancellationException) {
                hideProgress()
            } catch (e: Throwable) {
                onError(e)
                hideProgress()
            }
        }
    }

    protected suspend fun <X> background(context: CoroutineContext = backgroundContext, block: suspend () -> X): Deferred<X> {
        return async(context) {
            block.invoke()
        }
    }
}