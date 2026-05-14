package com.jalsanchay.tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentCyan,
    tertiary = TextSecondary,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = TextPrimary,
    onSecondary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    outline = BorderBlue
)

@Composable
fun JalSanchayTrackerTheme(
    darkTheme: Boolean = true, // Force dark theme as per requirements
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = DarkBackground
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
