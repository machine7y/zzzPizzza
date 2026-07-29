package com.example.zzzpizzza.domain.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PizzaTest {

    private val smallVariant = variant(
        price = 10.5,
        size = "S",
    )
    private val largeVariant = variant(
        price = 14.5,
        size = "L",
    )

    @Test
    fun `getDefaultVariant returns variant of default size when pizza has it`() {
        val pizza = pizza(
            defaultSize = "L",
            variantList = listOf(smallVariant, largeVariant),
        )

        val defaultVariant = pizza.getDefaultVariant()

        assertThat(defaultVariant).isEqualTo(largeVariant)
    }

    @Test
    fun `getDefaultVariant returns first variant when pizza has no variant of default size`() {
        val pizza = pizza(
            defaultSize = "M",
            variantList = listOf(smallVariant, largeVariant),
        )

        val defaultVariant = pizza.getDefaultVariant()

        assertThat(defaultVariant).isEqualTo(smallVariant)
    }

    @Test
    fun `getDefaultVariant returns null when pizza has no variants`() {
        val pizza = pizza(variantList = emptyList())

        val defaultVariant = pizza.getDefaultVariant()

        assertThat(defaultVariant).isNull()
    }
}
