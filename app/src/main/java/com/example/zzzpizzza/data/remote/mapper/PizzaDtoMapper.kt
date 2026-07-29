package com.example.zzzpizzza.data.remote.mapper

import com.example.zzzpizzza.data.remote.dto.PizzaDto
import com.example.zzzpizzza.data.remote.dto.PizzaListDto
import com.example.zzzpizzza.data.remote.dto.VariantDto
import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.domain.entity.Variant

fun PizzaListDto.toDomain() = pizzaList.map { it.toDomain() }

fun PizzaDto.toDomain() = Pizza(
    id = id,
    name = name,
    imageUrl = imageUrl,
    defaultSize = defaultSize,
    description = description,
    variantList = variantList.map { it.toDomain() },
)

fun VariantDto.toDomain() = Variant(
    price = price,
    size = size,
)
