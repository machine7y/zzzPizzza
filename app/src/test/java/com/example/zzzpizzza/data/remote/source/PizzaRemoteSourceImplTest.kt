package com.example.zzzpizzza.data.remote.source

import com.example.zzzpizzza.data.remote.dto.PizzaDto
import com.example.zzzpizzza.data.remote.dto.PizzaListDto
import com.example.zzzpizzza.data.remote.dto.VariantDto
import com.example.zzzpizzza.data.remote.mapper.toDomain
import com.example.zzzpizzza.data.remote.service.PizzaServiceApi
import com.example.zzzpizzza.domain.base.network.ResultValue
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PizzaRemoteSourceImplTest {

    private val pizzaServiceApi = mockk<PizzaServiceApi>()

    @Test
    fun `getPizzaList returns mapped pizza list when api returns pizza list`() = runTest {
        val pizzaListDto = pizzaListDto()
        coEvery { pizzaServiceApi.loadPizzaList() } returns pizzaListDto
        val source = createPizzaRemoteSource()

        val result = source.getPizzaList()

        assertThat(result).isEqualTo(ResultValue.Success(pizzaListDto.toDomain()))
    }

    @Test
    fun `getPizzaList returns error with api exception when api throws`() = runTest {
        val exception = IOException()
        coEvery { pizzaServiceApi.loadPizzaList() } throws exception
        val source = createPizzaRemoteSource()

        val result = source.getPizzaList()

        // Throwable has no value equality, so this compares the exception by identity.
        assertThat(result).isEqualTo(ResultValue.Error(exception))
    }

    @Test
    fun `getPizzaList rethrows cancellation exception when api call is cancelled`() = runTest {
        val exception = CancellationException()
        coEvery { pizzaServiceApi.loadPizzaList() } throws exception
        val source = createPizzaRemoteSource()

        val thrownException = runCatching { source.getPizzaList() }.exceptionOrNull()

        assertThat(thrownException).isSameInstanceAs(exception)
    }

    private fun createPizzaRemoteSource() = PizzaRemoteSourceImpl(pizzaServiceApi = pizzaServiceApi)

    private fun pizzaListDto(
        pizzaList: List<PizzaDto> = listOf(pizzaDto()),
    ) = PizzaListDto(pizzaList = pizzaList)

    private fun pizzaDto(
        id: String = "id",
        name: String = "name",
        imageUrl: String = "imageUrl",
        defaultSize: String = "defaultSize",
        description: String = "description",
        variantList: List<VariantDto> = listOf(variantDto()),
    ) = PizzaDto(
        id = id,
        name = name,
        imageUrl = imageUrl,
        defaultSize = defaultSize,
        description = description,
        variantList = variantList,
    )

    private fun variantDto(
        price: Double = 10.5,
        size: String = "size",
    ) = VariantDto(
        price = price,
        size = size,
    )
}
