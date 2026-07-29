package com.example.zzzpizzza.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VariantDto(
    @param:Json(name = "price")
    val price: Double,
    @param:Json(name = "size")
    val size: String,
)
