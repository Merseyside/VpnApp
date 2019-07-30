package com.merseyside.dropletapp.domain.base

import kotlinx.coroutines.CoroutineDispatcher

internal expect val networkContext: CoroutineDispatcher

internal expect val applicationContext: CoroutineDispatcher