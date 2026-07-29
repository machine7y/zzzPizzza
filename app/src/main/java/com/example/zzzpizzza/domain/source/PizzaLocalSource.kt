package com.example.zzzpizzza.domain.source

import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.Pizza

interface PizzaLocalSource {

    suspend fun savePizzaList(pizzaList: List<Pizza>)

    suspend fun loadPizzaList(): ResultValue<List<Pizza>>
}
