package com.merseyside.dropletapp.utils

import com.squareup.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

@ExperimentalCoroutinesApi
@JvmName("toFlow")
fun <T : Any> Query<T>.asFlow(): Flow<Query<T>> = callbackFlow<Query<T>> {
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            offer(this@asFlow)
        }
    }
    addListener(listener)
    offer(this@asFlow)
    awaitClose {
        removeListener(listener)
    }
}.conflate()

@ExperimentalCoroutinesApi
@JvmName("toFlowAddOnly")
fun <T : Any> Query<T>.asFlowAddOnly(): Flow<List<T>> = callbackFlow<List<T>> {
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            val list = this@asFlowAddOnly.executeAsList()
            offer(listOf(list.last()))
        }
    }
    addListener(listener)
    offer(this@asFlowAddOnly.executeAsList())
    awaitClose {
        removeListener(listener)
    }
}.conflate()

@ExperimentalCoroutinesApi
@JvmOverloads
fun <T : Any> Flow<Query<T>>.mapToOne(
    context: CoroutineContext = Dispatchers.Default
): Flow<T> = map {
    withContext(context) {
        it.executeAsOne()
    }
}

@ExperimentalCoroutinesApi
@JvmOverloads
fun <T : Any> Flow<Query<T>>.mapToOneOrDefault(
    defaultValue: T,
    context: CoroutineContext = Dispatchers.Default
): Flow<T> = map {
    withContext(context) { it.executeAsOneOrNull() } ?: defaultValue
}

@ExperimentalCoroutinesApi
@JvmOverloads
fun <T : Any> Flow<Query<T>>.mapToOneOrNull(
    context: CoroutineContext = Dispatchers.Default
): Flow<T?> = map { withContext(context) { it.executeAsOneOrNull() } }

@ExperimentalCoroutinesApi
@JvmOverloads
fun <T : Any> Flow<Query<T>>.mapToOneNotNull(
    context: CoroutineContext = Dispatchers.Default
): Flow<T> = mapNotNull { withContext(context) { it.executeAsOneOrNull() } }

@ExperimentalCoroutinesApi
@JvmOverloads
fun <T : Any> Flow<Query<T>>.mapToList(
    context: CoroutineContext = Dispatchers.Default
): Flow<List<T>> = map { withContext(context) { it.executeAsList() } }