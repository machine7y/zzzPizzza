package com.example.zzzpizzza.data.local.source

import com.example.zzzpizzza.data.local.db.dao.PizzaDao
import com.example.zzzpizzza.data.local.mapper.toDomain
import com.example.zzzpizzza.data.local.mapper.toEntity
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.source.PizzaLocalSource
import javax.inject.Inject

private const val EMPTY_PIZZA_DATA_MESSAGE = "Pizza data is empty"

class PizzaLocalSourceImpl @Inject constructor(
    private val pizzaDao: PizzaDao,
) : PizzaLocalSource {

    override suspend fun savePizzaList(pizzaList: List<Pizza>) {
        pizzaDao.replacePizzaWithVariantList(pizzaList.map { it.toEntity() })
    }

    override suspend fun loadPizzaList(): ResultValue<List<Pizza>> {
        val pizzaWithVariantList = pizzaDao.getPizzaWithVariantList()

        return if (pizzaWithVariantList.isEmpty()) {
            ResultValue.Error(Exception(EMPTY_PIZZA_DATA_MESSAGE))
        } else {
            ResultValue.Success(pizzaWithVariantList.map { it.toDomain() })
        }
    }
}
