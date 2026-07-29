package com.example.zzzpizzza.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.zzzpizzza.presentation.theme.ShadowColor

private val SHADOW_RADIUS = 12.dp
private val SHADOW_SPREAD = 0.dp
private val SHADOW_OFFSET = DpOffset(x = 2.dp, y = 4.dp)

@Composable
fun createShadow(): Shadow = Shadow(
    radius = SHADOW_RADIUS,
    spread = SHADOW_SPREAD,
    color = ShadowColor,
    offset = SHADOW_OFFSET,
)
