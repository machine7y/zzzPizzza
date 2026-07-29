package com.example.zzzpizzza.data.local.source

import com.example.zzzpizzza.data.local.db.dao.PizzaDao
import com.example.zzzpizzza.data.local.mapper.toEntity
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.example.zzzpizzza.domain.entity.pizza
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PizzaLocalSourceImplTest {

    private val pizzaDao = mockk<PizzaDao>()

    @Test
    fun `loadPizzaList returns stored pizza list when dao returns one pizza`() = runTest {
        val pizza = pizza()
        coEvery { pizzaDao.getPizzaWithVariantList() } returns listOf(pizza.toEntity())
        val source = createPizzaLocalSource()

        val result = source.loadPizzaList()

        assertThat(result).isEqualTo(ResultValue.Success(listOf(pizza)))
    }

    @Test
    fun `loadPizzaList returns empty data error when dao returns no pizzas`() = runTest {
        coEvery { pizzaDao.getPizzaWithVariantList() } returns emptyList()
        val source = createPizzaLocalSource()

        val result = source.loadPizzaList()

        assertThat(result).isInstanceOf(ResultValue.Error::class.java)
        assertThat((result as ResultValue.Error).exception).hasMessageThat().isEqualTo("Pizza data is empty")
    }

    @Test
    fun `savePizzaList replaces stored pizzas with mapped pizza list when pizza list is saved`() = runTest {
        val pizzaList = listOf(pizza())
        coJustRun { pizzaDao.replacePizzaWithVariantList(any()) }
        val source = createPizzaLocalSource()

        source.savePizzaList(pizzaList)

        coVerify(exactly = 1) { pizzaDao.replacePizzaWithVariantList(pizzaList.map { it.toEntity() }) }
    }

    private fun createPizzaLocalSource() = PizzaLocalSourceImpl(pizzaDao = pizzaDao)
}
