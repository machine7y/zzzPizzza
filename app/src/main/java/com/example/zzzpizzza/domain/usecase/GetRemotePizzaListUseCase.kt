package com.example.zzzpizzza.domain.usecase

import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.base.usecase.BaseNoParamsUseCase
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.source.PizzaRemoteSource
import javax.inject.Inject

class GetRemotePizzaListUseCase @Inject constructor(
    private val pizzaRemoteSource: PizzaRemoteSource,
) : BaseNoParamsUseCase<ResultValue<List<Pizza>>>() {

    override suspend fun execute() = pizzaRemoteSource.getPizzaList()
}
