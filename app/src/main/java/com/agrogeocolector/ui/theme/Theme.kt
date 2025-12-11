package com.agrogeocolector.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cores do tema AgroColetor (verde agronômico)
private val md_theme_light_primary = Color(0xFF4CAF50)
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
private val md_theme_light_secondary = Color(0xFF8BC34A)
private val md_theme_light_onSecondary = Color(0xFF000000)
private val md_theme_light_background = Color(0xFFFFFBFE)
private val md_theme_light_surface = Color(0xFFFFFBFE)

private val md_theme_dark_primary = Color(0xFF81C784)
private val md_theme_dark_onPrimary = Color(0xFF003300)
private val md_theme_dark_secondary = Color(0xFF9CCC65)
private val md_theme_dark_onSecondary = Color(0xFF000000)
private val md_theme_dark_background = Color(0xFF1B1B1F)
private val md_theme_dark_surface = Color(0xFF1B1B1F)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    background = md_theme_light_background,
    surface = md_theme_light_surface
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface
)

@Composable
fun AgroColetorTheme(
    darkTheme: Boolean = false, // Pode usar isSystemInDarkTheme()
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
