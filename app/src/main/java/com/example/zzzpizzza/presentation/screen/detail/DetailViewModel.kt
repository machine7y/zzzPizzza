package com.example.zzzpizzza.presentation.screen.detail

import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.usecase.GetLocalPizzaListUseCase
import com.example.zzzpizzza.presentation.base.mvvm.BaseViewModel
import com.example.zzzpizzza.presentation.entity.PizzaUi
import com.example.zzzpizzza.presentation.entity.VariantUi
import com.example.zzzpizzza.presentation.screen.detail.DetailLabel.InternalLoadingException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val MIN_PIZZA_COUNT = 1
private const val MAX_PIZZA_COUNT = 99
private const val MIN_PIZZA_LIST_SIZE_TO_SELECT_SECOND = 3
private const val FIRST_PIZZA_INDEX = 0
private const val SECOND_PIZZA_INDEX = 1

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getLocalPizzaListUseCase: GetLocalPizzaListUseCase,
) : BaseViewModel<DetailUiState, DetailInternalState, DetailEvent, DetailLabel>(
    initialState = DetailUiState(),
    initialInternalState = DetailInternalState(),
) {

    init {
        loadLocalPizzaData()
    }

    override fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.OnPizzaSelected -> onPizzaSelected(event.pizzaUrl)
            is DetailEvent.OnSizeClicked -> onSizeClicked(event)
            DetailEvent.OnMinusClicked -> updateState { addToCount(-1) }
            DetailEvent.OnPlusClicked -> updateState { addToCount(1) }
        }
    }

    private fun onPizzaSelected(newSelectedPizzaUrl: String) {
        val pizza = state.pizzaList.find { it.imageUrl == newSelectedPizzaUrl } ?: return

        val size = pizza
            .variantList
            .find { it.isSelected }
            ?.size ?: ""
        val price = internalState.getPrice(pizza.id, size)
        val title = internalState.pizzaList.find { it.id == pizza.id }?.name ?: ""

        updateState {
            copy(
                title = title,
                isZoomMode = if (getSelectedPizza().id == pizza.id) !isZoomMode else false,
                selectedPizzaId = pizza.id,
                fullPrice = price?.let { (it * pizzaCount).toFloat() } ?: 0f,
            )
        }
    }

    private fun onSizeClicked(event: DetailEvent.OnSizeClicked) {
        updateState {
            val newPizzaList = pizzaList.map { pizza ->
                if (pizza.id == selectedPizzaId) {
                    pizza.copy(
                        variantList = pizza.variantList.map { variantUi ->
                            variantUi.copy(isSelected = variantUi.size == event.size)
                        },
                    )
                } else {
                    pizza
                }
            }
            val price = internalState.getPrice(selectedPizzaId, event.size)

            copy(
                pizzaList = newPizzaList,
                fullPrice = price?.let { (it * pizzaCount).toFloat() } ?: 0f,
            )
        }
    }

    private fun loadLocalPizzaData() = launch {
        // A pizza without sizes has nothing to select or price, so the screen can't show it.
        val pizzaList = when (val pizzaListResult = getLocalPizzaListUseCase()) {
            is ResultValue.Success -> pizzaListResult.data.filter { it.variantList.isNotEmpty() }
            is ResultValue.Error -> emptyList()
        }

        if (pizzaList.isEmpty()) {
            publishLabel(InternalLoadingException)
        } else {
            showPizzaList(pizzaList)
        }
    }

    private fun showPizzaList(pizzaList: List<Pizza>) {
        updateInternalState { copy(pizzaList = pizzaList) }
        updateState {
            val selectedPizzaIndex = if (pizzaList.size >= MIN_PIZZA_LIST_SIZE_TO_SELECT_SECOND) {
                SECOND_PIZZA_INDEX
            } else {
                FIRST_PIZZA_INDEX
            }
            val selectedPizza = pizzaList[selectedPizzaIndex]

            copy(
                title = selectedPizza.name,
                selectedPizzaId = selectedPizza.id,
                pizzaList = pizzaList.mapToUi(),
                pizzaCount = MIN_PIZZA_COUNT,
                fullPrice = selectedPizza.getDefaultVariant()?.price?.toFloat() ?: 0f,
            )
        }
    }

    private fun DetailUiState.addToCount(delta: Int): DetailUiState {
        val selectedPizza = getSelectedPizza()
        val selectedVariant = selectedPizza.getSelectedVariant()
        val newPizzaCount = (pizzaCount + delta).coerceIn(MIN_PIZZA_COUNT, MAX_PIZZA_COUNT)
        val variantPrice = internalState.getPrice(selectedPizza.id, selectedVariant.size)

        return variantPrice?.let {
            copy(
                pizzaCount = newPizzaCount,
                fullPrice = (variantPrice * newPizzaCount).toFloat(),
            )
        } ?: copy()
    }

    private fun DetailInternalState.getPrice(selectedPizzaId: String, size: String): Double? =
        pizzaList.find { it.id == selectedPizzaId }
            ?.variantList
            ?.find { it.size == size }
            ?.price

    private fun List<Pizza>.mapToUi() = map { pizza ->
        val defaultSize = pizza.getDefaultVariant()?.size

        PizzaUi(
            id = pizza.id,
            name = pizza.name,
            imageUrl = pizza.imageUrl,
            description = pizza.description,
            variantList = pizza.variantList.map { variant ->
                VariantUi(
                    size = variant.size,
                    isSelected = variant.size == defaultSize,
                )
            },
        )
    }
}
