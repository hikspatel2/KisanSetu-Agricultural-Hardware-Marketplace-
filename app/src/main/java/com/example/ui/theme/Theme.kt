package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = AgriGreenLight,
    onPrimary = Color.Black,
    primaryContainer = AgriGreenDark,
    onPrimaryContainer = Color.White,
    secondary = HarvestGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = OnHarvestGoldContainer,
    onSecondaryContainer = HarvestGoldContainer,
    tertiary = IrrigationTeal,
    background = Color(0xFF121512),
    surface = Color(0xFF1B201C),
    surfaceVariant = Color(0xFF28302A),
    onBackground = Color(0xFFE2E7E2),
    onSurface = Color(0xFFE2E7E2)
)

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = OnAgriGreenContainer,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = OnHarvestGoldContainer,
    tertiary = IrrigationTeal,
    onTertiary = Color.White,
    tertiaryContainer = IrrigationTealContainer,
    background = AgriBackground,
    surface = AgriSurface,
    surfaceVariant = AgriSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = AgriBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent agricultural branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
