package com.example.zzzpizzza.data.remote.source

import com.example.zzzpizzza.data.remote.mapper.toDomain
import com.example.zzzpizzza.data.remote.service.PizzaServiceApi
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.source.PizzaRemoteSource
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class PizzaRemoteSourceImpl @Inject constructor(
    private val pizzaServiceApi: PizzaServiceApi,
) : PizzaRemoteSource {

    override suspend fun getPizzaList() = try {
        val response = pizzaServiceApi.loadPizzaList()

        val data = response.toDomain()
        ResultValue.Success(data)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ResultValue.Error(e)
    }
}
