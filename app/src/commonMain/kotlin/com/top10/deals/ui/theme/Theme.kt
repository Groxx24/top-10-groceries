package com.top10.deals.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Green for fresh produce, with an orange accent for the deal label, light or dark following the
 * phone. Every role is set in both schemes, so nothing falls back to Material's purple baseline;
 * the neutrals are tinted green to match. The window background in `androidMain/res/values{,-night}`
 * is [LightColors]' and [DarkColors]' background, so keep them in step.
 */
@Composable
fun Top10Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E6B3A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8EBCB),
    onPrimaryContainer = Color(0xFF0A3816),
    inversePrimary = Color(0xFF96D69E),
    secondary = Color(0xFF516351),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E8D1),
    onSecondaryContainer = Color(0xFF0F1F11),
    tertiary = Color(0xFF8A5100),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB8),
    onTertiaryContainer = Color(0xFF5A3200),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FAF4),
    onBackground = Color(0xFF181D18),
    surface = Color(0xFFF7FAF4),
    onSurface = Color(0xFF181D18),
    surfaceVariant = Color(0xFFDDE5DA),
    onSurfaceVariant = Color(0xFF414941),
    surfaceTint = Color(0xFF2E6B3A),
    inverseSurface = Color(0xFF2D322C),
    inverseOnSurface = Color(0xFFEEF2EA),
    outline = Color(0xFF717970),
    outlineVariant = Color(0xFFC1C9BE),
    scrim = Color.Black,
    surfaceBright = Color(0xFFF7FAF4),
    surfaceDim = Color(0xFFD7DBD5),
    // Cards are white on the off-white background.
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFEFF3EB),
    surfaceContainerHigh = Color(0xFFEAEFE6),
    surfaceContainerHighest = Color(0xFFE5EBE1),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF96D69E),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF1F4F2A),
    onPrimaryContainer = Color(0xFFC8EBCB),
    inversePrimary = Color(0xFF2E6B3A),
    secondary = Color(0xFFB7CCB5),
    onSecondary = Color(0xFF233425),
    secondaryContainer = Color(0xFF394B3A),
    onSecondaryContainer = Color(0xFFD3E8D1),
    tertiary = Color(0xFFFFB95F),
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF6B4200),
    onTertiaryContainer = Color(0xFFFFDDB8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111411),
    onBackground = Color(0xFFE0E4DC),
    surface = Color(0xFF111411),
    onSurface = Color(0xFFE0E4DC),
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFC1C9BE),
    surfaceTint = Color(0xFF96D69E),
    inverseSurface = Color(0xFFE0E4DC),
    inverseOnSurface = Color(0xFF2D322C),
    outline = Color(0xFF8B9389),
    outlineVariant = Color(0xFF414941),
    scrim = Color.Black,
    surfaceBright = Color(0xFF363B35),
    surfaceDim = Color(0xFF111411),
    surfaceContainerLowest = Color(0xFF0C0F0C),
    surfaceContainerLow = Color(0xFF1B201B),
    surfaceContainer = Color(0xFF1F241F),
    surfaceContainerHigh = Color(0xFF252B25),
    surfaceContainerHighest = Color(0xFF2A302A),
)
