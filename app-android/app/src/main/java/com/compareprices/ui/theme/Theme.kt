package com.compareprices.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
  primary = Primary,
  secondary = Secondary,
  background = BackgroundLight,
  surface = BackgroundLight
)

private val DarkColors = darkColorScheme(
  primary = Primary,
  secondary = Secondary,
  background = BackgroundDark,
  surface = BackgroundDark
)

@Composable
fun ComparePricesTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) DarkColors else LightColors

  MaterialTheme(
    colorScheme = colors,
    typography = AppTypography,
    content = content
  )
}