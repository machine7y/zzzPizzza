package com.example.zzzpizzza.presentation.utils

private const val DEFAULT_SPREAD = 1.052f
private const val CENTER_POINT = 0.5f

fun distributePoints(count: Int, spread: Float = DEFAULT_SPREAD) = if (count <= 0) {
    emptyList()
} else {
    (1..count).map { index ->
        val base = index.toFloat() / (count + 1)
        CENTER_POINT + (base - CENTER_POINT) * spread
    }
}
