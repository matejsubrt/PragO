package com.example.prago.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = Color.White,
    secondary = LighterBlue,
    onSecondary = Color.White,
    tertiary = AccentOrange,
    onTertiary = Color.White,
    background = Gray47,
    onBackground = Color.White,
    surface = Gray3D,
    onSurface = Color.White,
    primaryContainer = Gray70,
    onPrimaryContainer = Color.White,
    secondaryContainer = Gray7A,
    onSecondaryContainer = Color.White,
    tertiaryContainer = Color(0xFF585858),
    onTertiaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    secondary = Color.White,
    tertiary = DarkBlue,
    background = White,
    surface = Gray08,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun PragOTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = true,
        content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
    )
}