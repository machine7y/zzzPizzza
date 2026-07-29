package com.example.zzzpizzza.data.remote.service

import com.example.zzzpizzza.data.remote.dto.PizzaListDto
import retrofit2.http.GET

interface PizzaServiceApi {

    @GET("pizzas")
    suspend fun loadPizzaList(): PizzaListDto
}
