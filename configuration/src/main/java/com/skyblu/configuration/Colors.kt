package com.skyblu.configuration


import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.pow

/**
 * A file containing the colours used throughout the app
 */

val WALKING_COLOR = Color(0xFF000000)
val AIRCRAFT_COLOR = Color(0xFF53E2F1)
val FREEFALL_COLOR = Color(0xFF2644FF)
val CANOPY_COLOR = Color(0xFFE64A19)
val LANDED_COLOR = Color(0xFF000000)

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val ThemeBlueOne = Color(0xFF00A8FF)
val ThemeBlueTwo = Color(0xFF00A8FF)

val ThemeBlueGradient = Brush.verticalGradient(
    listOf<Color>(ThemeBlueOne, ThemeBlueTwo)
)

@get:Composable
val Colors.warning: Color
    get() = if (isLight) Color(0xFFffc587) else Color(0xFFffc587)

fun Colors.warning() : Color{
    return if (isLight) Color(0xFFffc587) else Color(0xFFffc587)
}

fun Colors.success() : Color{
    return  if (isLight) Color(0xFFabffa3) else Color(0xFF60b36a)
}


val DarkColorPalette = darkColors(
    primary = ThemeBlueOne,
    primaryVariant = ThemeBlueOne,
    secondary = ThemeBlueTwo,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.DarkGray
)

val LightColorPalette = lightColors(
    primary = ThemeBlueOne,
    primaryVariant = ThemeBlueOne,
    secondary = Teal200,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.LightGray
)


