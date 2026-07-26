package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkNewsColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = WhatsAppGreen,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    background = NewsBackgroundDark,
    onBackground = TextPrimary,
    surface = NewsSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = NewsSurfaceVariantDark,
    onSurfaceVariant = TextSecondary
)

private val LightNewsColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = WhatsAppGreenDark,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun KshanaKshanamTheme(
    darkTheme: Boolean = true, // Default dark theme for media news cards
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkNewsColorScheme else LightNewsColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
