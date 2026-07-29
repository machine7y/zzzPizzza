package com.example.zzzpizzza.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PizzaDto(
    @param:Json(name = "id")
    val id: String,
    @param:Json(name = "name")
    val name: String,
    @param:Json(name = "image_url")
    val imageUrl: String,
    @param:Json(name = "default_size")
    val defaultSize: String,
    @param:Json(name = "description")
    val description: String,
    @param:Json(name = "variants")
    val variantList: List<VariantDto>,
)
