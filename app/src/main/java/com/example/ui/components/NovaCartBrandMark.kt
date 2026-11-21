package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralDark
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PureWhite

@Composable
fun NovaCartLogoEmblem(
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  containerColor: Color = ObsidianInk,
  bagColor: Color = PureWhite,
  sparkColor: Color = NovaCoral,
) {
  Box(
    modifier = modifier.size(size).background(containerColor, RoundedCornerShape(size * 0.28f)),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(size * 0.64f)) {
      val w = this.size.width
      val h = this.size.height

      // Handle arc
      drawArc(
        color = bagColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(w * 0.28f, h * 0.05f),
        size = Size(w * 0.44f, h * 0.34f),
        style = Stroke(width = w * 0.09f, cap = StrokeCap.Round),
      )

      // Sleek tapered shopping bag body
      val bagPath =
        Path().apply {
          moveTo(w * 0.16f, h * 0.28f)
          lineTo(w * 0.84f, h * 0.28f)
          lineTo(w * 0.90f, h * 0.88f)
          lineTo(w * 0.10f, h * 0.88f)
          close()
        }
      drawPath(bagPath, color = bagColor)

      // Crisp 4-point Nova Star Spark in the upper-right of the bag
      val cx = w * 0.68f
      val cy = h * 0.44f
      val outerR = w * 0.24f
      val innerR = w * 0.07f
      val sparkPath =
        Path().apply {
          moveTo(cx, cy - outerR)
          lineTo(cx + innerR, cy - innerR)
          lineTo(cx + outerR, cy)
          lineTo(cx + innerR, cy + innerR)
          lineTo(cx, cy + outerR)
          lineTo(cx - innerR, cy + innerR)
          lineTo(cx - outerR, cy)
          lineTo(cx - innerR, cy - innerR)
          close()
        }
      drawPath(sparkPath, color = containerColor)
      val innerSpark =
        Path().apply {
          val r = outerR * 0.82f
          val ir = innerR * 0.78f
          moveTo(cx, cy - r)
          lineTo(cx + ir, cy - ir)
          lineTo(cx + r, cy)
          lineTo(cx + ir, cy + ir)
          lineTo(cx, cy + r)
          lineTo(cx - ir, cy + ir)
          lineTo(cx - r, cy)
          lineTo(cx - ir, cy - ir)
          close()
        }
      drawPath(innerSpark, color = sparkColor)

      // Subtle base accent line
      drawRoundRect(
        color = containerColor,
        topLeft = Offset(w * 0.26f, h * 0.74f),
        size = Size(w * 0.28f, h * 0.055f),
        cornerRadius = CornerRadius(4f, 4f),
      )
    }
  }
}

@Composable
fun NovaCartWordmarkHeader(
  modifier: Modifier = Modifier,
  showSubtitle: Boolean = true,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    NovaCartLogoEmblem(size = 42.dp)
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "NOVA",
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          letterSpacing = 1.6.sp,
          color = ObsidianInk,
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = "CART",
          fontFamily = OutfitFontFamily,
          fontWeight = FontWeight.SemiBold,
          fontSize = 20.sp,
          letterSpacing = 1.6.sp,
          color = NovaCoral,
        )
      }
      if (showSubtitle) {
        Text(
          text = "mynovacart.in",
          style = MaterialTheme.typography.bodySmall,
          color = MutedSlate,
        )
      }
    }
  }
}

@Composable
fun PulsingComingSoonPill(
  modifier: Modifier = Modifier,
  label: String = "COMING SOON",
) {
  val infiniteTransition = rememberInfiniteTransition(label = "coming_soon_pulse")
  val dotAlpha by
    infiniteTransition.animateFloat(
      initialValue = 0.35f,
      targetValue = 1.0f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "pulse_alpha",
    )

  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = NovaCoralSoft,
    border = BorderStroke(1.dp, NovaCoral.copy(alpha = 0.25f)),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Box(
        modifier =
          Modifier.size(7.dp).alpha(dotAlpha).background(color = NovaCoral, shape = CircleShape)
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = NovaCoralDark,
      )
    }
  }
}

@Composable
fun SectionDividerLine(modifier: Modifier = Modifier) {
  Box(modifier = modifier.background(HairlineBorder))
}
