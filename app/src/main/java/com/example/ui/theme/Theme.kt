package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/*
 * Copyright (c) 2026, Sam Moreno.
 * Licensed under the BSD 2-Clause License.
 */

// 1. Esquema Escuro Sóbrio (Matte Charcoal)
private val SoberDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = ColorWhite,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkPrimaryVariant,
    onSecondary = ColorWhite,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = ColorError
)

// 2. Esquema Claro Minimalista (Clean Off-White)
private val SoberLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = ColorWhite,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = LightPrimaryVariant,
    onSecondary = ColorWhite,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = ColorError
)

// 3. Esquema Azul Sóbrio (Slate Blue)
private val SoberSlateColorScheme = darkColorScheme(
    primary = SlatePrimary,
    onPrimary = ColorWhite,
    primaryContainer = SlateSurfaceVariant,
    onPrimaryContainer = SlatePrimary,
    secondary = SlatePrimaryVariant,
    onSecondary = ColorWhite,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    error = ColorError
)

fun getThemeColorScheme(themeMode: AppThemeMode): ColorScheme {
    return when (themeMode) {
        AppThemeMode.DARK -> SoberDarkColorScheme
        AppThemeMode.LIGHT -> SoberLightColorScheme
        AppThemeMode.SLATE_BLUE -> SoberSlateColorScheme
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(themeMode)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insets = WindowCompat.getInsetsController(window, view)
            val isLight = themeMode == AppThemeMode.LIGHT
            insets.isAppearanceLightStatusBars = isLight
            insets.isAppearanceLightNavigationBars = isLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
