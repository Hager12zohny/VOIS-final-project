package com.example.githubsearch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ---- Blue / Grey palette ----
val SlateBlue90 = Color(0xFFE8EEF6)
val SlateBlue80 = Color(0xFFC7D5E6)
val SteelBlue60 = Color(0xFF5B7EA6)
val DeepBlue40  = Color(0xFF2C4A6E)
val DeepBlue30  = Color(0xFF1E3450)
val Slate20     = Color(0xFF20242B)
val Slate30     = Color(0xFF2E333C)
val Slate70     = Color(0xFF9AA5B1)
val Ash95       = Color(0xFFF5F7FA)
val AccentTeal  = Color(0xFF3AAFA9)

private val LightColors = lightColorScheme(
    primary = DeepBlue40,
    onPrimary = Color.White,
    primaryContainer = SlateBlue80,
    onPrimaryContainer = DeepBlue30,
    secondary = SteelBlue60,
    onSecondary = Color.White,
    tertiary = AccentTeal,
    background = Ash95,
    onBackground = Slate20,
    surface = Color.White,
    onSurface = Slate20,
    surfaceVariant = SlateBlue90,
    onSurfaceVariant = Slate70,
    outline = SlateBlue80,
    error = Color(0xFFB3261E)
)

private val DarkColors = darkColorScheme(
    primary = SlateBlue80,
    onPrimary = DeepBlue30,
    primaryContainer = DeepBlue40,
    onPrimaryContainer = SlateBlue90,
    secondary = SteelBlue60,
    onSecondary = Color.White,
    tertiary = AccentTeal,
    background = Slate20,
    onBackground = SlateBlue90,
    surface = Slate30,
    onSurface = SlateBlue90,
    surfaceVariant = Color(0xFF3A4049),
    onSurfaceVariant = Slate70,
    outline = Color(0xFF4A5361),
    error = Color(0xFFF2B8B5)
)

@Composable
fun GitHubSearchAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}