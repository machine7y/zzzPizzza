package com.example.zzzpizzza.domain.entity

fun pizza(
    id: String = "id",
    name: String = "name",
    imageUrl: String = "imageUrl",
    defaultSize: String = "defaultSize",
    description: String = "description",
    variantList: List<Variant> = listOf(variant()),
) = Pizza(
    id = id,
    name = name,
    imageUrl = imageUrl,
    defaultSize = defaultSize,
    description = description,
    variantList = variantList,
)

fun variant(
    price: Double = 10.5,
    size: String = "size",
) = Variant(
    price = price,
    size = size,
)
