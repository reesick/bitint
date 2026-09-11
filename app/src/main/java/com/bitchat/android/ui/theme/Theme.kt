package com.bitchat.android.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

// Standard UI semantics live in Material so stock components and custom Bitchat composables
// share one source of truth. LocalBitchatPalette below only supplies app-specific extra colors.
internal val DarkBitchatColorScheme = darkColorScheme(
    primary = Color(0xFF00A884),
    onPrimary = Color(0xFF04120E),
    primaryContainer = Color(0xFF005C4B),
    onPrimaryContainer = Color(0xFFD7F5EC),
    secondary = Color(0xFF53BDEB),
    onSecondary = Color(0xFF04161D),
    secondaryContainer = Color(0xFF0F3B4C),
    onSecondaryContainer = Color(0xFFCDEBF9),
    tertiary = DarkBitchatPalette.accentOrange,
    onTertiary = Color.Black,
    background = Color(0xFF0B141A),
    onBackground = Color(0xFFE9EDEF),
    surface = Color(0xFF111B21),
    onSurface = Color(0xFFE9EDEF),
    surfaceVariant = Color(0xFF202C33),
    onSurfaceVariant = Color(0xFF8696A0),
    outline = Color(0xFF2A3942),
    outlineVariant = Color(0xFF1F2C34),
    error = Color(0xFFF15C6D),
    onError = Color.Black
)

internal val LightBitchatColorScheme = lightColorScheme(
    primary = Color(0xFF12A150),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9FDD3),
    onPrimaryContainer = Color(0xFF0B2F18),
    secondary = Color(0xFF027EB5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6EEFA),
    onSecondaryContainer = Color(0xFF033049),
    tertiary = LightBitchatPalette.accentOrange,
    onTertiary = Color.Black,
    background = Color(0xFFEFE7DE),
    onBackground = Color(0xFF111B21),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111B21),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF667781),
    outline = Color(0xFFD1D7DB),
    outlineVariant = Color(0xFFE9EDEF),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun BitchatTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    // App-level override from ThemePreferenceManager
    val themePref by ThemePreferenceManager.themeFlow.collectAsState(initial = ThemePreference.System)
    val shouldUseDark = when (darkTheme) {
        true -> true
        false -> false
        null -> when (themePref) {
            ThemePreference.Dark -> true
            ThemePreference.Light -> false
            ThemePreference.System -> isSystemInDarkTheme()
        }
    }

    val colorScheme = if (shouldUseDark) DarkBitchatColorScheme else LightBitchatColorScheme
    val palette = if (shouldUseDark) DarkBitchatPalette else LightBitchatPalette

    val view = LocalView.current
    SideEffect {
        (view.context as? Activity)?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    if (!shouldUseDark) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = if (!shouldUseDark) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else 0
            }
            window.navigationBarColor = colorScheme.surface.toArgb()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    CompositionLocalProvider(LocalBitchatPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
