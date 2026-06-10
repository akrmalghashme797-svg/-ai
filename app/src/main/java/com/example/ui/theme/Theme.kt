package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = OliveGreenPrimary,
    secondary = OliveGreenAccent,
    tertiary = GoldenGold,
    background = DarkBackground,
    surface = DarkCharcoal,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = DarkBackground,
    onBackground = PureWhite,
    onSurface = PureWhite
  )

private val LightColorScheme = DarkColorScheme // Force dark theme for Al-Tofan branding!

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  // Force specific custom colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
