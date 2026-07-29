package com.example.zzzpizzza.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PizzaListDto(
    @param:Json(name = "pizzas")
    val pizzaList: List<PizzaDto>,
)
