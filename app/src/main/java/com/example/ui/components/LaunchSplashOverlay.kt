package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PureWhite

@Composable
fun LaunchSplashOverlay(
  visible: Boolean,
  onDismiss: () -> Unit,
) {
  val logoScale = remember { Animatable(0.74f) }
  val contentAlpha = remember { Animatable(0f) }

  LaunchedEffect(visible) {
    if (visible) {
      logoScale.animateTo(
        targetValue = 1f,
        animationSpec =
          spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
          ),
      )
    }
  }

  LaunchedEffect(visible) {
    if (visible) {
      contentAlpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
      )
    }
  }

  AnimatedVisibility(
    visible = visible,
    exit = fadeOut(animationSpec = tween(durationMillis = 360)),
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(
            brush =
              Brush.verticalGradient(
                colors = listOf(PureWhite, PureWhite, NovaCoralSoft.copy(alpha = 0.45f))
              )
          )
          .statusBarsPadding()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss,
          ),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 32.dp).alpha(contentAlpha.value),
      ) {
        NovaCartLogoEmblem(
          size = 88.dp,
          modifier = Modifier.scale(logoScale.value),
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "NOVA",
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = 3.sp,
            color = ObsidianInk,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CART",
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            letterSpacing = 3.sp,
            color = NovaCoral,
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PulsingComingSoonPill(label = "OFFICIAL STORE")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "mynovacart.in",
          style = MaterialTheme.typography.bodyMedium,
          color = MutedSlate,
        )
      }
    }
  }
}
