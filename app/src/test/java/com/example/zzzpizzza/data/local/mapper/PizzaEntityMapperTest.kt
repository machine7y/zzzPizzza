package com.example.zzzpizzza.data.local.mapper

import com.example.zzzpizzza.data.local.db.entity.PizzaEntity
import com.example.zzzpizzza.data.local.db.entity.VariantEntity
import com.example.zzzpizzza.data.local.db.entity.join.PizzaWithVariantList
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.entity.Variant
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PizzaEntityMapperTest {

    private val pizza = Pizza(
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
    )

    private val pizzaWithVariantList = PizzaWithVariantList(
        pizza = PizzaEntity(
            id = "1",
            name = "Margherita",
            imageUrl = "https://example.com/margherita.png",
            defaultSize = "M",
            description = "Tomato and mozzarella",
        ),
        variantList = listOf(
            VariantEntity(
                pizzaId = "1",
                price = 10.5,
                size = "S",
            ),
            VariantEntity(
                pizzaId = "1",
                price = 12.5,
                size = "M",
            ),
        ),
    )

    @Test
    fun `toEntity maps every field and links variants to pizza id when pizza is mapped`() {
        val entity = pizza.toEntity()

        assertThat(entity).isEqualTo(pizzaWithVariantList)
    }

    @Test
    fun `toDomain maps every field of pizza and its variants when pizza with variant list is mapped`() {
        val domainPizza = pizzaWithVariantList.toDomain()

        assertThat(domainPizza).isEqualTo(pizza)
    }
}
