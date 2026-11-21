package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun LaunchHubScreen(
  uiState: NovaCartUiState,
  onCheckStatusClick: () -> Unit,
  onNotifyMeClick: () -> Unit,
  onVisitWebsiteClick: () -> Unit,
  onShareClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val status = uiState.storeStatus

  Box(modifier = modifier.fillMaxSize().background(PureWhite), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Launch Hub",
            style = MaterialTheme.typography.headlineMedium,
            color = ObsidianInk,
          )
          Text(
            text = "Shopify Storefront readiness & launch notifications",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedSlate,
          )
        }
      }

      // Live Store Readiness Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = NovaCoralSoft,
                  modifier = Modifier.size(42.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Outlined.CloudSync,
                      contentDescription = null,
                      tint = NovaCoral,
                    )
                  }
                }
                Column {
                  Text(
                    text = "Store Target",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedSlate,
                  )
                  Text(
                    text = NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianInk,
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color =
                  when (status.isWebsiteReachable) {
                    true -> EmeraldSoft
                    else -> NovaCoralSoft
                  },
              ) {
                Text(
                  text =
                    when {
                      status.isChecking -> "CHECKING"
                      status.isStoreLaunched -> "LIVE"
                      status.isWebsiteReachable == true -> "ONLINE • PRE-LAUNCH"
                      else -> "COMING SOON"
                    },
                  style = MaterialTheme.typography.labelSmall,
                  color = if (status.isWebsiteReachable == true) EmeraldLive else NovaCoral,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = status.statusHeadline,
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
              )
              Text(
                text = status.statusDetail,
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
              )
            }

            Button(
              onClick = onCheckStatusClick,
              enabled = !status.isChecking,
              modifier =
                Modifier.fillMaxWidth()
                  .height(48.dp)
                  .minimumInteractiveComponentSize()
                  .testTag("check_status_button"),
              shape = RoundedCornerShape(12.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = ObsidianInk,
                  contentColor = PureWhite,
                ),
            ) {
              if (status.isChecking) {
                CircularProgressIndicator(
                  color = PureWhite,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Checking mynovacart.in...", style = MaterialTheme.typography.labelLarge)
              } else {
                Icon(
                  imageVector = Icons.Outlined.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = stringResource(R.string.cta_check_status),
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }
          }
        }
      }

      // Your Launch Alert Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors =
            CardDefaults.cardColors(
              containerColor =
                if (uiState.registration.isSubscribed) EmeraldSoft else PureWhite
            ),
          border =
            BorderStroke(
              1.dp,
              if (uiState.registration.isSubscribed) EmeraldLive.copy(alpha = 0.25f)
              else HairlineBorder,
            ),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(
                imageVector =
                  if (uiState.registration.isSubscribed) Icons.Outlined.CheckCircle
                  else Icons.Outlined.NotificationsActive,
                contentDescription = null,
                tint = if (uiState.registration.isSubscribed) EmeraldLive else NovaCoral,
              )
              Text(
                text =
                  if (uiState.registration.isSubscribed) "Early Launch Alert Active"
                  else "Launch Notification Not Set",
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
              )
            }

            Text(
              text =
                if (uiState.registration.isSubscribed) {
                  "Registered email: ${uiState.registration.email} (${uiState.registration.interestedCategoryIds.size} departments selected)."
                } else {
                  "Save your email to get notified as soon as mynovacart.in opens for shopping."
                },
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate,
            )

            OutlinedButton(
              onClick = onNotifyMeClick,
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("hub_notify_button"),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, ObsidianInk),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
            ) {
              Text(
                text =
                  if (uiState.registration.isSubscribed) "Edit Notification Preferences"
                  else "Notify Me at Launch",
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }
        }
      }

      // Shopify Integration Architecture Summary
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Shopify Storefront Architecture",
            style = MaterialTheme.typography.titleLarge,
            color = ObsidianInk,
          )
          Text(
            text =
              "Built to sync directly with your existing Shopify store at mynovacart.in with no duplicate database.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedSlate,
          )

          NovaCartCatalogBlueprint.shopifySyncCapabilities.forEach { capability ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = AlabasterSurface,
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.VerifiedUser,
                  contentDescription = null,
                  tint = ObsidianInk,
                  modifier = Modifier.size(20.dp).padding(top = 2.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = capability.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = ObsidianInk,
                  )
                  Text(
                    text = capability.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                  )
                }
              }
            }
          }
        }
      }

      // Quick Website & Share Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          OutlinedButton(
            onClick = onVisitWebsiteClick,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
          ) {
            Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Website", style = MaterialTheme.typography.labelLarge)
          }
          OutlinedButton(
            onClick = onShareClick,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
          ) {
            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share App", style = MaterialTheme.typography.labelLarge)
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}
