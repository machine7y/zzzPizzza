package com.example.zzzpizzza.data.remote.mapper

import com.example.zzzpizzza.data.remote.dto.PizzaDto
import com.example.zzzpizzza.data.remote.dto.PizzaListDto
import com.example.zzzpizzza.data.remote.dto.VariantDto
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.entity.Variant
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PizzaDtoMapperTest {

    @Test
    fun `toDomain maps every field of pizza and its variants when pizza list dto is mapped`() {
        val pizzaListDto = PizzaListDto(
            pizzaList = listOf(
                PizzaDto(
                    id = "1",
                    name = "Margherita",
                    imageUrl = "https://example.com/margherita.png",
                    defaultSize = "M",
                    description = "Tomato and mozzarella",
                    variantList = listOf(
                        VariantDto(
                            price = 10.5,
                            size = "S",
                        ),
                        VariantDto(
                            price = 12.5,
                            size = "M",
                        ),
                    ),
                ),
            ),
        )
        val expectedPizzaList = listOf(
            Pizza(
                id = "1",
                name = "Margherita",
                imageUrl = "https://example.com/margherita.png",
                defaultSize = "M",
                description = "Tomato and mozzarella",
                variantList = listOf(
                    Variant(
                        price = 10.5,
                        size = "S",
                    ),
                    Variant(
                        price = 12.5,
                        size = "M",
                    ),
                ),
            ),
        )

        val pizzaList = pizzaListDto.toDomain()

        assertThat(pizzaList).isEqualTo(expectedPizzaList)
    }
}
