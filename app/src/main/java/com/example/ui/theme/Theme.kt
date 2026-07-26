package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NestPrimaryAccentHex,
    onPrimary = NestCard,
    primaryContainer = NestSecondaryBackground,
    onPrimaryContainer = NestPrimaryText,
    secondary = NestSecondaryAccent,
    onSecondary = NestCard,
    secondaryContainer = NestSecondaryBackground,
    onSecondaryContainer = NestPrimaryText,
    tertiary = NestGoldHighlight,
    background = NestBackground,
    onBackground = NestPrimaryText,
    surface = NestCard,
    onSurface = NestPrimaryText,
    surfaceVariant = NestSecondaryBackground,
    onSurfaceVariant = NestSecondaryText,
    outline = NestBorder,
    outlineVariant = NestBorder,
    error = NestError,
    onError = NestCard
)

private val DarkColorScheme = darkColorScheme(
    primary = NestDarkPrimaryAccent,
    onPrimary = NestDarkCard,
    primaryContainer = NestDarkSecondaryBackground,
    onPrimaryContainer = NestDarkPrimaryText,
    secondary = NestDarkSecondaryAccent,
    onSecondary = NestDarkCard,
    secondaryContainer = NestDarkSecondaryBackground,
    onSecondaryContainer = NestDarkPrimaryText,
    tertiary = NestGoldHighlight,
    background = NestDarkBackground,
    onBackground = NestDarkPrimaryText,
    surface = NestDarkCard,
    onSurface = NestDarkPrimaryText,
    surfaceVariant = NestDarkSecondaryBackground,
    onSurfaceVariant = NestDarkSecondaryText,
    outline = NestDarkBorder,
    outlineVariant = NestDarkBorder,
    error = NestDarkError,
    onError = NestDarkCard
)

@Composable
fun NestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) {
        NestCustomColors(
            background = NestDarkBackground,
            secondaryBackground = NestDarkSecondaryBackground,
            card = NestDarkCard,
            primaryAccent = NestDarkPrimaryAccent,
            secondaryAccent = NestDarkSecondaryAccent,
            success = NestDarkSuccess,
            warning = NestDarkWarning,
            error = NestDarkError,
            primaryText = NestDarkPrimaryText,
            secondaryText = NestDarkSecondaryText,
            border = NestDarkBorder,
            overlay = Color(0x66000000),
            isDark = true
        )
    } else {
        NestCustomColors(isDark = false)
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalNestColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
