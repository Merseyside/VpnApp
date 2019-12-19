package com.merseyside.dropletapp.presentation.view.activity.main.model

import android.content.Context
import android.os.Bundle
import com.merseyside.dropletapp.domain.Server
import com.merseyside.dropletapp.domain.interactor.GetDropletsInteractor
import com.merseyside.dropletapp.presentation.base.BaseDropletViewModel
import com.merseyside.dropletapp.presentation.navigation.Screens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import ru.terrakok.cicerone.Router
import kotlin.coroutines.CoroutineContext

@UseExperimental(InternalCoroutinesApi::class)
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

    private val dropletObserver = object : FlowCollector<List<Server>> {
        override suspend fun emit(value: List<Server>) {
            if (value.isNotEmpty()) {
                navigateToDropletListScreen()
            } else {
                navigateToAuthScreen()
            }

            job.cancel()
        }
    }

    init {
        launch {
            getDropletsUseCase.observe().collect(dropletObserver)
        }
    }

    fun navigateToTokenScreen() {
        router.newRootScreen(Screens.TokenScreen())
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