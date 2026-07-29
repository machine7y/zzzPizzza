// Template. Replace the Foo* and Item names with the class under test and its types, this line with the package,
// and add the import of MainDispatcherRule from the package it was copied to.

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FooViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fooRepository = mockk<FooRepository>()

    @Test
    fun `load shows items when repository returns items`() = runTest {
        val itemList = listOf(item(id = 1), item(id = 2))
        coEvery { fooRepository.getItemList() } returns itemList
        val viewModel = createFooViewModel()

        viewModel.onEvent(FooEvent.Load)

        assertThat(viewModel.state.value).isEqualTo(FooState(itemList = itemList))
    }

    @Test
    fun `load sends error event when repository throws`() = runTest {
        coEvery { fooRepository.getItemList() } throws IOException()
        val viewModel = createFooViewModel()

        viewModel.eventFlow.test {
            viewModel.onEvent(FooEvent.Load)

            assertThat(awaitItem()).isEqualTo(FooUiEvent.ShowError)
        }
        coVerify(exactly = 1) { fooRepository.getItemList() }
    }

    @Test
    fun `delete removes item in repository when item is selected`() = runTest {
        val selectedItem = item(id = 1)
        coJustRun { fooRepository.delete(any()) }
        val viewModel = createFooViewModel()

        viewModel.onEvent(FooEvent.Delete(itemId = selectedItem.id))

        coVerify(exactly = 1) { fooRepository.delete(selectedItem.id) }
    }

    private fun createFooViewModel() = FooViewModel(fooRepository = fooRepository)

    private fun item(
        id: Long = 1,
        title: String = "title",
        description: String = "description",
    ) = Item(
        id = id,
        title = title,
        description = description,
    )
}
