package com.project.binar.okariru.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
private fun okariruLightColorScheme() = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorOnPrimary,
    primaryContainer = ColorPrimaryContainer,
    onPrimaryContainer = ColorOnPrimaryContainer,
    secondary = ColorSecondary,
    onSecondary = ColorOnSecondary,
    error = ColorError,
    onError = ColorOnError,
    background = ColorBackground,
    onBackground = ColorOnBackground,
    surface = ColorSurface,
    onSurface = ColorOnSurface,
    surfaceVariant = ColorSurfaceVariant,
    onSurfaceVariant = ColorOnSurfaceVariant,
    outline = ColorOutline
)



@Composable
fun OkariruTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = okariruLightColorScheme(),
        typography = Typography,
        content = content
    )
}