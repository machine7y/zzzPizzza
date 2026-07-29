package com.example.zzzpizzza.presentation.screen.detail

import app.cash.turbine.test
import com.example.zzzpizzza.MainDispatcherRule
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.pizza
import com.example.zzzpizzza.domain.entity.variant
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import com.example.zzzpizzza.domain.usecase.GetLocalPizzaListUseCase
import com.example.zzzpizzza.presentation.entity.PizzaUi
import com.example.zzzpizzza.presentation.entity.VariantUi
import com.example.zzzpizzza.presentation.entity.VariantUi.Companion.SIZE_LARGE
import com.example.zzzpizzza.presentation.entity.VariantUi.Companion.SIZE_MEDIUM
import com.example.zzzpizzza.presentation.entity.VariantUi.Companion.SIZE_SMALL
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnMinusClicked
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnPizzaSelected
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnPlusClicked
import com.example.zzzpizzza.presentation.screen.detail.DetailEvent.OnSizeClicked
import com.example.zzzpizzza.presentation.screen.detail.DetailLabel.InternalLoadingException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

private const val MAX_PIZZA_COUNT = 99

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    // The loading error label is published from init, so the test has to subscribe before init's coroutine runs.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val pizzaLocalSource = mockk<PizzaLocalSource>()

    private val margherita = pizza(
        id = "margherita",
        name = "Margherita",
        imageUrl = "margherita.png",
        defaultSize = SIZE_SMALL,
        description = "Margherita description",
        variantList = listOf(
            variant(
                price = 10.5,
                size = SIZE_SMALL,
            ),
            variant(
                price = 14.5,
                size = SIZE_LARGE,
            ),
        ),
    )

    private val pepperoni = pizza(
        id = "pepperoni",
        name = "Pepperoni",
        imageUrl = "pepperoni.png",
        defaultSize = SIZE_LARGE,
        description = "Pepperoni description",
        variantList = listOf(
            variant(
                price = 12.25,
                size = SIZE_SMALL,
            ),
            variant(
                price = 16.75,
                size = SIZE_LARGE,
            ),
        ),
    )

    // Hawaiian defaults to large, so a small size click that leaks from pepperoni to other pizzas changes the state.
    private val hawaiian = pizza(
        id = "hawaiian",
        name = "Hawaiian",
        imageUrl = "hawaiian.png",
        defaultSize = SIZE_LARGE,
        description = "Hawaiian description",
        variantList = listOf(
            variant(
                price = 11.5,
                size = SIZE_SMALL,
            ),
            variant(
                price = 15.5,
                size = SIZE_LARGE,
            ),
        ),
    )

    private val threePizzaList = listOf(margherita, pepperoni, hawaiian)

    private val pizzaWithoutSizes = pizza(
        id = "empty",
        name = "Empty",
        imageUrl = "empty.png",
        variantList = emptyList(),
    )

    private val margheritaUi = PizzaUi(
        id = "margherita",
        name = "Margherita",
        imageUrl = "margherita.png",
        description = "Margherita description",
        variantList = listOf(
            VariantUi(
                size = SIZE_SMALL,
                isSelected = true,
            ),
            VariantUi(
                size = SIZE_LARGE,
                isSelected = false,
            ),
        ),
    )

    private val pepperoniUi = PizzaUi(
        id = "pepperoni",
        name = "Pepperoni",
        imageUrl = "pepperoni.png",
        description = "Pepperoni description",
        variantList = listOf(
            VariantUi(
                size = SIZE_SMALL,
                isSelected = false,
            ),
            VariantUi(
                size = SIZE_LARGE,
                isSelected = true,
            ),
        ),
    )

    private val pepperoniSmallUi = pepperoniUi.copy(
        variantList = listOf(
            VariantUi(
                size = SIZE_SMALL,
                isSelected = true,
            ),
            VariantUi(
                size = SIZE_LARGE,
                isSelected = false,
            ),
        ),
    )

    private val hawaiianUi = PizzaUi(
        id = "hawaiian",
        name = "Hawaiian",
        imageUrl = "hawaiian.png",
        description = "Hawaiian description",
        variantList = listOf(
            VariantUi(
                size = SIZE_SMALL,
                isSelected = false,
            ),
            VariantUi(
                size = SIZE_LARGE,
                isSelected = true,
            ),
        ),
    )

    private val threePizzaState = DetailUiState(
        title = "Pepperoni",
        selectedPizzaId = "pepperoni",
        pizzaList = listOf(margheritaUi, pepperoniUi, hawaiianUi),
        pizzaCount = 1,
        fullPrice = 16.75f,
        isZoomMode = false,
    )

    @Test
    fun `load shows second pizza with its default size when local source returns three pizzas`() = runTest {
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(threePizzaList)
        val viewModel = createDetailViewModel()

        runCurrent()

        assertThat(viewModel.state).isEqualTo(threePizzaState)
    }

    @Test
    fun `load shows first pizza when local source returns two pizzas`() = runTest {
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(listOf(margherita, pepperoni))
        val viewModel = createDetailViewModel()
        val expectedState = DetailUiState(
            title = "Margherita",
            selectedPizzaId = "margherita",
            pizzaList = listOf(margheritaUi, pepperoniUi),
            pizzaCount = 1,
            fullPrice = 10.5f,
            isZoomMode = false,
        )

        runCurrent()

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `load selects first size and its price when pizza has no variant of default size`() = runTest {
        val pizzaList = listOf(pepperoni.copy(defaultSize = SIZE_MEDIUM))
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(pizzaList)
        val viewModel = createDetailViewModel()
        val expectedState = DetailUiState(
            title = "Pepperoni",
            selectedPizzaId = "pepperoni",
            pizzaList = listOf(pepperoniSmallUi),
            pizzaCount = 1,
            fullPrice = 12.25f,
            isZoomMode = false,
        )

        runCurrent()

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `load leaves out pizza without sizes when local source returns it`() = runTest {
        val pizzaList = listOf(margherita, pizzaWithoutSizes, pepperoni)
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(pizzaList)
        val viewModel = createDetailViewModel()
        val expectedState = DetailUiState(
            title = "Margherita",
            selectedPizzaId = "margherita",
            pizzaList = listOf(margheritaUi, pepperoniUi),
            pizzaCount = 1,
            fullPrice = 10.5f,
            isZoomMode = false,
        )

        runCurrent()

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `load publishes loading error and keeps empty state when every pizza has no sizes`() = runTest {
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(listOf(pizzaWithoutSizes))
        val viewModel = createDetailViewModel()

        viewModel.labelFlow.test {
            runCurrent()

            assertThat(awaitItem()).isEqualTo(InternalLoadingException)
        }
        assertThat(viewModel.state).isEqualTo(DetailUiState())
    }

    @Test
    fun `load publishes loading error and keeps empty state when local source returns error`() = runTest {
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Error(IOException())
        val viewModel = createDetailViewModel()

        viewModel.labelFlow.test {
            runCurrent()

            assertThat(awaitItem()).isEqualTo(InternalLoadingException)
        }
        assertThat(viewModel.state).isEqualTo(DetailUiState())
    }

    @Test
    fun `pizza click selects clicked pizza with price for count when another pizza is clicked`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnPlusClicked)
        val expectedState = threePizzaState.copy(
            title = "Margherita",
            selectedPizzaId = "margherita",
            pizzaCount = 2,
            fullPrice = 21f,
        )

        viewModel.onEvent(OnPizzaSelected(margherita.imageUrl))

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `pizza click turns zoom on when selected pizza is clicked`() = runTest {
        val viewModel = createViewModelWithThreePizzas()

        viewModel.onEvent(OnPizzaSelected(pepperoni.imageUrl))

        assertThat(viewModel.state).isEqualTo(threePizzaState.copy(isZoomMode = true))
    }

    @Test
    fun `pizza click turns zoom off when selected pizza is clicked in zoom mode`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnPizzaSelected(pepperoni.imageUrl))

        viewModel.onEvent(OnPizzaSelected(pepperoni.imageUrl))

        assertThat(viewModel.state).isEqualTo(threePizzaState)
    }

    @Test
    fun `pizza click turns zoom off when another pizza is clicked in zoom mode`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnPizzaSelected(pepperoni.imageUrl))
        val expectedState = threePizzaState.copy(
            title = "Margherita",
            selectedPizzaId = "margherita",
            fullPrice = 10.5f,
            isZoomMode = false,
        )

        viewModel.onEvent(OnPizzaSelected(margherita.imageUrl))

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `pizza click shows price of selected size when clicked pizza has non default size selected`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnSizeClicked(SIZE_SMALL))
        viewModel.onEvent(OnPizzaSelected(margherita.imageUrl))
        val expectedState = threePizzaState.copy(
            pizzaList = listOf(margheritaUi, pepperoniSmallUi, hawaiianUi),
            fullPrice = 12.25f,
        )

        viewModel.onEvent(OnPizzaSelected(pepperoni.imageUrl))

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `size click selects size and shows its price for count when selected pizza has this size`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnPlusClicked)
        val expectedState = threePizzaState.copy(
            pizzaList = listOf(margheritaUi, pepperoniSmallUi, hawaiianUi),
            pizzaCount = 2,
            fullPrice = 24.5f,
        )

        viewModel.onEvent(OnSizeClicked(SIZE_SMALL))

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `plus click increases count and price of selected size when count is below max`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        viewModel.onEvent(OnSizeClicked(SIZE_SMALL))
        val clickCountToOneBelowMax = MAX_PIZZA_COUNT - 2
        repeat(clickCountToOneBelowMax) { viewModel.onEvent(OnPlusClicked) }
        val expectedState = threePizzaState.copy(
            pizzaList = listOf(margheritaUi, pepperoniSmallUi, hawaiianUi),
            pizzaCount = MAX_PIZZA_COUNT,
            fullPrice = 1212.75f,
        )

        viewModel.onEvent(OnPlusClicked)

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `plus click keeps count at max when count is max`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        repeat(MAX_PIZZA_COUNT - 1) { viewModel.onEvent(OnPlusClicked) }
        val expectedState = threePizzaState.copy(
            pizzaCount = MAX_PIZZA_COUNT,
            fullPrice = 1658.25f,
        )

        viewModel.onEvent(OnPlusClicked)

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `minus click decreases count by one and updates price when count is above min`() = runTest {
        val viewModel = createViewModelWithThreePizzas()
        // Starting from three, a step of two would be clamped to the min and look like a step of one.
        repeat(2) { viewModel.onEvent(OnPlusClicked) }
        val expectedState = threePizzaState.copy(
            pizzaCount = 2,
            fullPrice = 33.5f,
        )

        viewModel.onEvent(OnMinusClicked)

        assertThat(viewModel.state).isEqualTo(expectedState)
    }

    @Test
    fun `minus click keeps count at min when count is min`() = runTest {
        val viewModel = createViewModelWithThreePizzas()

        viewModel.onEvent(OnMinusClicked)

        assertThat(viewModel.state).isEqualTo(threePizzaState)
    }

    private fun createDetailViewModel() = DetailViewModel(
        getLocalPizzaListUseCase = GetLocalPizzaListUseCase(pizzaLocalSource = pizzaLocalSource),
    )

    private fun TestScope.createViewModelWithThreePizzas(): DetailViewModel {
        coEvery { pizzaLocalSource.loadPizzaList() } returns ResultValue.Success(threePizzaList)
        val viewModel = createDetailViewModel()
        runCurrent()

        return viewModel
    }
}
