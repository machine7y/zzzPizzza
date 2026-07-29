package com.example.zzzpizzza.domain.usecase

import com.example.zzzpizzza.domain.base.usecase.BaseParamsUseCase
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import javax.inject.Inject

class SaveLocalPizzaListUseCase @Inject constructor(
    private val pizzaLocalSource: PizzaLocalSource,
) : BaseParamsUseCase<List<Pizza>, Unit>() {

    override suspend fun execute(param: List<Pizza>) {
        pizzaLocalSource.savePizzaList(param)
    }
}
