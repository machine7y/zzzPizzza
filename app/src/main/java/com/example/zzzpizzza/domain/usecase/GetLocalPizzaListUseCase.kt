package com.example.zzzpizzza.domain.usecase

import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.base.usecase.BaseNoParamsUseCase
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import javax.inject.Inject

class GetLocalPizzaListUseCase @Inject constructor(
    private val pizzaLocalSource: PizzaLocalSource,
) : BaseNoParamsUseCase<ResultValue<List<Pizza>>>() {

    override suspend fun execute() = pizzaLocalSource.loadPizzaList()
}
