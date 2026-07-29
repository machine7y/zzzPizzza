package com.example.zzzpizzza.presentation.screen.splash

import app.cash.turbine.test
import com.arttttt.nav3router.Router
import com.example.zzzpizzza.MainDispatcherRule
import com.example.zzzpizzza.R
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.pizza
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import com.example.zzzpizzza.domain.source.PizzaRemoteSource
import com.example.zzzpizzza.domain.usecase.GetRemotePizzaListUseCase
import com.example.zzzpizzza.domain.usecase.SaveLocalPizzaListUseCase
import com.example.zzzpizzza.presentation.screen.Screen
import com.example.zzzpizzza.presentation.screen.splash.SplashEvent.OnAnimationEnd
import com.example.zzzpizzza.presentation.screen.splash.SplashLabel.NetworkExceptionMessage
import com.example.zzzpizzza.presentation.utils.StringResourceWrapper.ResourceWrapper
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

private const val ANIMATION_START_DELAY_MILLIS = 100L
private const val RETRY_DELAY_MILLIS = 60_000L

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    // The retry label is published from init, so the test has to subscribe before init's coroutine runs.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val pizzaRemoteSource = mockk<PizzaRemoteSource>()
    private val pizzaLocalSource = mockk<PizzaLocalSource>()
    private val router = mockk<Router<Screen>>()

    private val pizzaList = listOf(pizza())

    @Test
    fun `init starts animation when start delay passes`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } coAnswers { awaitCancellation() }
        val viewModel = createSplashViewModel()

        advanceTimeBy(ANIMATION_START_DELAY_MILLIS - 1)
        runCurrent()

        assertThat(viewModel.state).isEqualTo(SplashState(isAnimatePizza = false))

        advanceTimeBy(1)
        runCurrent()

        assertThat(viewModel.state).isEqualTo(SplashState(isAnimatePizza = true))
    }

    @Test
    fun `load saves pizza list when remote source returns pizza list`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } returns ResultValue.Success(pizzaList)
        coJustRun { pizzaLocalSource.savePizzaList(any()) }
        createSplashViewModel()

        runCurrent()

        coVerify(exactly = 1) { pizzaLocalSource.savePizzaList(pizzaList) }
    }

    @Test
    fun `load publishes retry message when remote source returns error`() = runTest {
        // The second result ends the retry loop, otherwise runTest would keep running it after the test body.
        coEvery { pizzaRemoteSource.getPizzaList() } returnsMany listOf(
            ResultValue.Error(IOException()),
            ResultValue.Success(pizzaList),
        )
        coJustRun { pizzaLocalSource.savePizzaList(any()) }
        val viewModel = createSplashViewModel()
        val expectedLabel = NetworkExceptionMessage(ResourceWrapper(R.string.splash_networkErrorRetry))

        viewModel.labelFlow.test {
            runCurrent()

            assertThat(awaitItem()).isEqualTo(expectedLabel)
        }
    }

    @Test
    fun `load retries after retry delay when remote source returns error`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } returnsMany listOf(
            ResultValue.Error(IOException()),
            ResultValue.Success(pizzaList),
        )
        coJustRun { pizzaLocalSource.savePizzaList(any()) }
        createSplashViewModel()
        runCurrent()

        advanceTimeBy(RETRY_DELAY_MILLIS - 1)
        runCurrent()

        coVerify(exactly = 1) { pizzaRemoteSource.getPizzaList() }

        advanceTimeBy(1)
        runCurrent()

        coVerify(exactly = 2) { pizzaRemoteSource.getPizzaList() }
    }

    @Test
    fun `animation end navigates to detail screen when pizza list is loaded`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } returns ResultValue.Success(pizzaList)
        coJustRun { pizzaLocalSource.savePizzaList(any()) }
        justRun { router.replaceCurrent(any()) }
        val viewModel = createSplashViewModel()
        runCurrent()

        viewModel.onEvent(OnAnimationEnd)
        runCurrent()

        verify(exactly = 1) { router.replaceCurrent(Screen.DetailScreen) }
    }

    @Test
    fun `load does not navigate when animation is not ended`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } returns ResultValue.Success(pizzaList)
        coJustRun { pizzaLocalSource.savePizzaList(any()) }
        justRun { router.replaceCurrent(any()) }
        createSplashViewModel()

        advanceUntilIdle()

        verify(exactly = 0) { router.replaceCurrent(any()) }
    }

    @Test
    fun `animation end does not navigate when remote source returns error`() = runTest {
        // The retry after the error never returns, so the loop ends and advanceUntilIdle can finish.
        coEvery { pizzaRemoteSource.getPizzaList() } returns ResultValue.Error(IOException()) coAndThen {
            awaitCancellation()
        }
        justRun { router.replaceCurrent(any()) }
        val viewModel = createSplashViewModel()
        runCurrent()

        viewModel.onEvent(OnAnimationEnd)
        advanceUntilIdle()

        verify(exactly = 0) { router.replaceCurrent(any()) }
    }

    @Test
    fun `animation end does not navigate when pizza list is still saving`() = runTest {
        coEvery { pizzaRemoteSource.getPizzaList() } returns ResultValue.Success(pizzaList)
        coEvery { pizzaLocalSource.savePizzaList(any()) } coAnswers { awaitCancellation() }
        justRun { router.replaceCurrent(any()) }
        val viewModel = createSplashViewModel()
        runCurrent()

        viewModel.onEvent(OnAnimationEnd)
        advanceUntilIdle()

        verify(exactly = 0) { router.replaceCurrent(any()) }
    }

    private fun createSplashViewModel() = SplashViewModel(
        getRemotePizzaListUseCase = GetRemotePizzaListUseCase(pizzaRemoteSource = pizzaRemoteSource),
        saveLocalPizzaListUseCase = SaveLocalPizzaListUseCase(pizzaLocalSource = pizzaLocalSource),
        router = router,
    )
}
