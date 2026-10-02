package com.top10.groceries.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.top10.groceries.di.AppContainer
import com.top10.groceries.ui.top.TopOffersRoute

@Composable
fun App(container: AppContainer) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors) {
        TopOffersRoute(container)
    }
}

// Green for fresh produce, with an orange accent for the deal label.
private val LightColors = lightColorScheme(
    primary = Color(0xFF2E6B3A),
    onPrimary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB8),
    onTertiaryContainer = Color(0xFF5A3200),
    background = Color(0xFFF7FAF4),
    surface = Color(0xFFF7FAF4),
    surfaceContainerHighest = Color(0xFFE5EBE1),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF96D69E),
    onPrimary = Color(0xFF003912),
    tertiaryContainer = Color(0xFF6B4200),
    onTertiaryContainer = Color(0xFFFFDDB8),
    background = Color(0xFF111411),
    surface = Color(0xFF111411),
    surfaceContainerHighest = Color(0xFF2A302A),
)
