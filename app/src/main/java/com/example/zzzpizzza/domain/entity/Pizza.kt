package com.example.zzzpizzza.domain.entity

data class Pizza(
    val id: String,
    val name: String,
    val imageUrl: String,
    val defaultSize: String,
    val description: String,
    val variantList: List<Variant>,
) {

    fun getDefaultVariant() = variantList.find { it.size == defaultSize } ?: variantList.firstOrNull()
}
