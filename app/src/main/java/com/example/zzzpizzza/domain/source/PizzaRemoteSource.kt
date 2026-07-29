package com.example.zzzpizzza.domain.source

import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.Pizza

interface PizzaRemoteSource {

    suspend fun getPizzaList(): ResultValue<List<Pizza>>
}
