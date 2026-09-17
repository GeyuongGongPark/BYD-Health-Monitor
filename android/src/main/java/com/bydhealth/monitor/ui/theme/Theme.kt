package com.bydhealth.monitor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BydBlueBright,
    onPrimary = BackgroundDark,
    primaryContainer = BydBlueDim,
    onPrimaryContainer = TextPrimary,
    secondary = HealthGreen,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = CriticalRed,
    onError = TextPrimary,
    errorContainer = CriticalRedContainer,
    onErrorContainer = CriticalRed,
    outline = DividerDark,
)

@Composable
fun BYDHealthMonitorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = BYDTypography,
        content = content,
    )
}
