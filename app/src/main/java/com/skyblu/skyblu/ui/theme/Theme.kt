package com.skyblu.skyblu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.skyblu.configuration.DarkColorPalette
import com.skyblu.configuration.LightColorPalette
import com.skyblu.userinterface.ui.theme.ThemeBlueOne
import com.skyblu.userinterface.ui.theme.ThemeBlueTwo



/**
 * Applies the theme for the application
 */
@Composable
fun SkybluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}