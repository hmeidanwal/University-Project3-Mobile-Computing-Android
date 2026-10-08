package com.example.pricecomparable.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = PrimaryMint,
    onPrimary = Color.White,

    secondary = PrimaryMintDark,
    onSecondary = Color.White,

    background = BgTop,
    onBackground = HeadingText,

    surface = GlassSurface,
    onSurface = HeadingText,

    outline = GlassBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryMint,
    onPrimary = Color.Black,

    secondary = PrimaryMintDark,
    onSecondary = Color.Black,

    background = Color(0xFF0F0F0F),
    onBackground = Color.White,

    surface = GlassSurfaceDark,
    onSurface = Color.White,

    outline = Color(0x33FFFFFF)
)

@Composable
fun PriceComparableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // ❗ Turn off dynamic color (we use custom theme)
    content: @Composable () -> Unit
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
