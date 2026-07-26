package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Nest Scandinavian Light Palette
val NestBackground = Color(0xFFF8F5EF)
val NestSecondaryBackground = Color(0xFFF2EDE3)
val NestCard = Color(0xFFFFFCF8)

val NestPrimaryAccent = Color(0xA08B5B).copy(alpha = 1f) // #A08B5B
val NestPrimaryAccentHex = Color(0xFFA08B5B)
val NestSecondaryAccent = Color(0xFF8C7A4F)

val NestSuccess = Color(0xFF6F8A5A)
val NestWarning = Color(0xFFC89D58)
val NestError = Color(0xFFC96B5B)

val NestPrimaryText = Color(0xFF2F2A23)
val NestSecondaryText = Color(0xFF6C665D)
val NestBorder = Color(0xFFE8E1D4)

// Nest Scandinavian Dark Palette
val NestDarkBackground = Color(0xFF141311)
val NestDarkSecondaryBackground = Color(0xFF1D1B17)
val NestDarkCard = Color(0xFF25221D)

val NestDarkPrimaryAccent = Color(0xFFC3AF84)
val NestDarkSecondaryAccent = Color(0xFFAB986E)

val NestDarkSuccess = Color(0xFF86A86C)
val NestDarkWarning = Color(0xFFDAAE62)
val NestDarkError = Color(0xFFDD7D6E)

val NestDarkPrimaryText = Color(0xFFEFEBE4)
val NestDarkSecondaryText = Color(0xFFA7A094)
val NestDarkBorder = Color(0xFF38332B)

// Additional semantic shades for luxury depth
val NestOverlay = Color(0x332F2A23)
val NestGoldHighlight = Color(0xFFC5B288)

@Immutable
data class NestCustomColors(
    val background: Color = NestBackground,
    val secondaryBackground: Color = NestSecondaryBackground,
    val card: Color = NestCard,
    val primaryAccent: Color = NestPrimaryAccentHex,
    val secondaryAccent: Color = NestSecondaryAccent,
    val success: Color = NestSuccess,
    val warning: Color = NestWarning,
    val error: Color = NestError,
    val primaryText: Color = NestPrimaryText,
    val secondaryText: Color = NestSecondaryText,
    val border: Color = NestBorder,
    val overlay: Color = NestOverlay,
    val isDark: Boolean = false
)

val LocalNestColors = staticCompositionLocalOf { NestCustomColors() }
