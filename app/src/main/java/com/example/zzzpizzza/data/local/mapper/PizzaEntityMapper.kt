package com.example.zzzpizzza.data.local.mapper

import com.example.zzzpizzza.data.local.db.entity.PizzaEntity
import com.example.zzzpizzza.data.local.db.entity.VariantEntity
import com.example.zzzpizzza.data.local.db.entity.join.PizzaWithVariantList
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.entity.Variant

fun Pizza.toEntity() = PizzaWithVariantList(
    pizza = PizzaEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        defaultSize = defaultSize,
        description = description,
    ),
    variantList = variantList.map { it.toEntity(pizzaId = id) },
)

fun Variant.toEntity(pizzaId: String) = VariantEntity(
    pizzaId = pizzaId,
    price = price,
    size = size,
)

fun PizzaWithVariantList.toDomain() = Pizza(
    id = pizza.id,
    name = pizza.name,
    imageUrl = pizza.imageUrl,
    defaultSize = pizza.defaultSize,
    description = pizza.description,
    variantList = variantList.map { it.toDomain() },
)

fun VariantEntity.toDomain() = Variant(
    price = price,
    size = size,
)
