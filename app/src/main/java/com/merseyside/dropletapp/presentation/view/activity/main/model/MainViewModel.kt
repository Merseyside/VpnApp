package com.merseyside.dropletapp.presentation.view.activity.main.model

import android.content.Context
import android.os.Bundle
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import com.merseyside.merseyLib.utils.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class)
class MainViewModel(
    private val router: Router,
    private val getDropletsUseCase: GetDropletsInteractor
) : BaseDropletViewModel(router), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, throwable -> }
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + coroutineExceptionHandler + job

    override fun updateLanguage(context: Context) {}

    override fun readFrom(bundle: Bundle) {}

    override fun writeTo(bundle: Bundle) {}

    override fun dispose() {
        getDropletsUseCase.cancel()
    }

    private var isInitialized = false

    private val dropletObserver = object : FlowCollector<List<Server>> {
        var prevSize = 0

        override suspend fun emit(value: List<Server>) {
            if (value.isNotEmpty()) {
                if (!isInitialized || (value.size == 1 && prevSize == 0)) navigateToDropletListScreen()
                prevSize = value.size
            } else {
                if (!isInitialized) {
                    navigateToAuthScreen()
                }
            }

            isInitialized = true
        }
    }

    init {
        launch {
            getDropletsUseCase.observe().collect(dropletObserver)
        }
    }

    fun navigateToDropletListScreen() {
        router.newRootScreen(Screens.DropletListScreen())
    }

    fun navigateToSettings() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun navigateToAuthScreen() {
        router.newRootScreen(Screens.AuthScreen())
    }
}