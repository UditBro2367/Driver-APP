package com.example.ui.theme

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

// High Density Theme Color Schemes
private val HighDensityColorScheme = lightColorScheme(
    primary = HDBluePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = HDBlueContainer,
    onPrimaryContainer = HDNavyDark,
    secondary = HDEmergencyRed,
    onSecondary = SurfaceWhite,
    secondaryContainer = HDEmergencyContainer,
    onSecondaryContainer = HDEmergencyText,
    tertiary = AmberWarning,
    onTertiary = SurfaceWhite,
    background = HDBackground,
    onBackground = HDTextPrimary,
    surface = HDSurface,
    onSurface = HDTextPrimary,
    surfaceVariant = HDSurfaceVariant,
    onSurfaceVariant = HDTextSecondary,
    error = HDEmergencyRed,
    onError = SurfaceWhite,
    outline = HDBorder
)

@Composable
fun AmbulanceDriverTheme(
    darkTheme: Boolean = false, // High Density Theme
    content: @Composable () -> Unit
) {
    val colorScheme = HighDensityColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HDSurface.toArgb()
            window.navigationBarColor = HDNavBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
