package com.example.zzzpizzza.presentation.entity

data class PizzaUi(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val variantList: List<VariantUi>,
) {

    fun getSelectedVariant() = variantList.first { it.isSelected }
}
