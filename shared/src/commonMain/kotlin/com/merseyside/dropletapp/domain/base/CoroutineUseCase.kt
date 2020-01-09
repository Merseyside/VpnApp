package com.merseyside.dropletapp.domain.base

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineUseCase<T, Params> : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var backgroundContext: CoroutineContext = networkContext

    protected abstract suspend fun executeOnBackground(params: Params?): T

    fun execute(
        onPreExecute: () -> Unit = {},
        onComplete: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        onPostExecute: () -> Unit = {},
        params: Params? = null
    ) {
        launch {
            onPreExecute()
            try {
                val result = withContext(backgroundContext) {
                    executeOnBackground(params)
                }
                onComplete.invoke(result)
                onPostExecute()
            } catch (e: CancellationException) {
                onPostExecute()
            } catch (e: Throwable) {
                onError(e)
                onPostExecute()
            }
        }
    }

    protected suspend fun <X> background(context: CoroutineContext = backgroundContext, block: suspend () -> X): Deferred<X> {
        return async(context) {
            block.invoke()
        }
    }
}