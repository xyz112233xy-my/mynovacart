package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val NovaLightColorScheme =
  lightColorScheme(
    primary = ObsidianInk,
    onPrimary = PureWhite,
    primaryContainer = NovaCoralSoft,
    onPrimaryContainer = NovaCoralDark,
    secondary = NovaCoral,
    onSecondary = PureWhite,
    secondaryContainer = StoneContainer,
    onSecondaryContainer = ObsidianInk,
    tertiary = NovaGold,
    onTertiary = PureWhite,
    tertiaryContainer = NovaGoldSoft,
    onTertiaryContainer = ObsidianInk,
    background = PureWhite,
    onBackground = ObsidianInk,
    surface = PureWhite,
    onSurface = ObsidianInk,
    surfaceVariant = AlabasterSurface,
    onSurfaceVariant = MutedSlate,
    outline = HairlineBorder,
    outlineVariant = StoneContainer,
  )

val NovaShapes =
  Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun NovaCartTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = NovaLightColorScheme,
    typography = NovaTypography,
    shapes = NovaShapes,
    content = content,
  )
}
