package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.LaunchCategory
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPreviewSheet(
  category: LaunchCategory?,
  isSubscribedToApp: Boolean,
  isInterestedInCategory: Boolean,
  onToggleCategoryAlert: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (category == null) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    contentColor = ObsidianInk,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = category.containerColor,
          modifier = Modifier.size(56.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = category.icon,
              contentDescription = category.title,
              tint = category.accentColor,
              modifier = Modifier.size(28.dp),
            )
          }
        }
        Column(modifier = Modifier.weight(1f)) {
          PulsingComingSoonPill(label = "LAUNCHING SOON")
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = category.title,
            style = MaterialTheme.typography.headlineSmall,
            color = ObsidianInk,
          )
        }
      }

      Text(
        text = category.launchExpectation,
        style = MaterialTheme.typography.bodyLarge,
        color = MutedSlate,
      )

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = AlabasterSurface,
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Icon(
            imageVector = Icons.Outlined.Storefront,
            contentDescription = null,
            tint = ObsidianInk,
          )
          Column {
            Text(
              text = "Shopify Collection Sync Ready",
              style = MaterialTheme.typography.titleSmall,
              color = ObsidianInk,
            )
            Text(
              text =
                "Live products, prices, and stock for ${category.title} will sync from mynovacart.in on launch day. No placeholder items are shown before launch.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )
          }
        }
      }

      Button(
        onClick = { onToggleCategoryAlert(category.id) },
        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("category_notify_button"),
        shape = RoundedCornerShape(14.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor =
              if (isSubscribedToApp && isInterestedInCategory) EmeraldLive else ObsidianInk,
            contentColor = PureWhite,
          ),
      ) {
        Icon(
          imageVector =
            if (isSubscribedToApp && isInterestedInCategory) Icons.Outlined.CheckCircle
            else Icons.Outlined.NotificationsActive,
          contentDescription = null,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text =
            when {
              isSubscribedToApp && isInterestedInCategory -> "Alert Enabled for ${category.title}"
              isSubscribedToApp -> "Add ${category.title} to My Alert"
              else -> "Notify Me When ${category.title} Opens"
            },
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}
