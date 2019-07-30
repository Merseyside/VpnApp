package com.merseyside.dropletapp.domain.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val networkContext: CoroutineDispatcher = Dispatchers.Default
internal actual val applicationContext: CoroutineDispatcher = Dispatchers.Main