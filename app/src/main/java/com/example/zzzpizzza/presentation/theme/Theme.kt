package com.example.zzzpizzza.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The design has a single light look, so there is no dark scheme and no dynamic color.
private val ColorScheme = lightColorScheme(
    primary = AccentColor,
    onPrimary = White,
    background = White,
    onBackground = ActiveColor,
    surface = White,
    onSurface = ActiveColor,
)

@Composable
fun ZzzPizzzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
