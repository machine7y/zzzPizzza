package com.example.zzzpizzza.presentation.screen.splash

import com.arttttt.nav3router.Router
import com.example.zzzpizzza.R
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.usecase.GetRemotePizzaListUseCase
import com.example.zzzpizzza.domain.usecase.SaveLocalPizzaListUseCase
import com.example.zzzpizzza.presentation.base.mvvm.BaseViewModel
import com.example.zzzpizzza.presentation.screen.Screen
import com.example.zzzpizzza.presentation.screen.splash.SplashLabel.NetworkExceptionMessage
import com.example.zzzpizzza.presentation.utils.StringResourceWrapper.ResourceWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getRemotePizzaListUseCase: GetRemotePizzaListUseCase,
    private val saveLocalPizzaListUseCase: SaveLocalPizzaListUseCase,
    private val router: Router<Screen>,
) : BaseViewModel<SplashState, SplashInternalState, SplashEvent, SplashLabel>(SplashState(), SplashInternalState()) {

    init {
        launchAnimation()
        loadPizzaList()

        observeInternalState()
    }

    override fun onEvent(event: SplashEvent) = when (event) {
        SplashEvent.OnAnimationEnd -> updateInternalState { copy(isAnimationFinished = true) }
    }

    private fun launchAnimation() = launch {
        delay(ANIMATION_START_DELAY)
        updateState { copy(isAnimatePizza = true) }
    }

    private fun loadPizzaList() = launch {
        var isLoaded = false

        while (!isLoaded) {
            when (val pizzaListResult = getRemotePizzaListUseCase()) {
                is ResultValue.Success -> {
                    saveLocalPizzaListUseCase(pizzaListResult.data)

                    updateInternalState { copy(isDataLoaded = true) }
                    isLoaded = true
                }
                is ResultValue.Error -> {
                    publishLabel(NetworkExceptionMessage(ResourceWrapper(R.string.splash_networkErrorRetry)))
                    delay(RETRY_DELAY)
                }
            }
        }
    }

    private fun observeInternalState() = launch {
        internalStateFlow.onEach(::checkLoadingAndAnimation).collect()
    }

    fun checkLoadingAndAnimation(internalState: SplashInternalState) {
        if (internalState.isAnimationFinished && internalState.isDataLoaded) {
            router.replaceCurrent(Screen.DetailScreen)
        }
    }

    companion object {

        private const val ANIMATION_START_DELAY = 100L
        private const val RETRY_DELAY = 60_000L
    }
}
