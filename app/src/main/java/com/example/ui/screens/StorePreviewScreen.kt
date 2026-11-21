package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FutureFeature
import com.example.ui.components.PulsingComingSoonPill
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralDark
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StoneContainer
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun StorePreviewScreen(
  uiState: NovaCartUiState,
  onInspectLockedFeature: (FutureFeature) -> Unit,
  onNotifyMeClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxSize().background(PureWhite), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Store Preview",
              style = MaterialTheme.typography.headlineMedium,
              color = ObsidianInk,
            )
            Text(
              text = "Prepared for mynovacart.in launch",
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate,
            )
          }
          PulsingComingSoonPill(label = "PRE-LAUNCH")
        }
      }

      // Disabled Search Bar Placeholder (no fake catalog queries)
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = AlabasterSurface,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth().testTag("disabled_search_bar"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = "Product search disabled until launch",
              tint = MutedSlate.copy(alpha = 0.65f),
            )
            Text(
              text = "Search products — Available at Launch",
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate.copy(alpha = 0.75f),
              modifier = Modifier.weight(1f),
            )
            Icon(
              imageVector = Icons.Outlined.Lock,
              contentDescription = "Locked",
              tint = MutedSlate.copy(alpha = 0.65f),
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }

      // Locked Storefront Notice Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = NovaCoralSoft),
          border = BorderStroke(1.dp, NovaCoral.copy(alpha = 0.22f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = NovaCoralDark,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = stringResource(R.string.store_locked_banner_title),
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
              )
            }
            Text(
              text = stringResource(R.string.store_locked_banner_body),
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate,
            )
          }
        }
      }

      // Quick Locked Modules Row (Cart, Wishlist, Account, Orders)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          LockedQuickTile(
            title = "Cart",
            icon = Icons.Outlined.ShoppingCart,
            onClick = {
              uiState.futureFeatures.find { it.id == "shopping_cart" }?.let(onInspectLockedFeature)
            },
            modifier = Modifier.weight(1f),
          )
          LockedQuickTile(
            title = "Wishlist",
            icon = Icons.Outlined.FavoriteBorder,
            onClick = {
              uiState.futureFeatures.find { it.id == "wishlist" }?.let(onInspectLockedFeature)
            },
            modifier = Modifier.weight(1f),
          )
          LockedQuickTile(
            title = "Account",
            icon = Icons.Outlined.AccountCircle,
            onClick = {
              uiState.futureFeatures.find { it.id == "customer_accounts" }?.let(onInspectLockedFeature)
            },
            modifier = Modifier.weight(1f),
          )
          LockedQuickTile(
            title = "Orders",
            icon = Icons.Outlined.LocalShipping,
            onClick = {
              uiState.futureFeatures.find { it.id == "order_tracking" }?.let(onInspectLockedFeature)
            },
            modifier = Modifier.weight(1f),
          )
        }
      }

      // All 10 Prepared Future Shopping Features (Disabled until Store Launch)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Prepared Shopping Capabilities",
            style = MaterialTheme.typography.titleLarge,
            color = ObsidianInk,
          )
          Text(
            text =
              "Tap any capability to inspect how it connects to Shopify once mynovacart.in opens.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedSlate,
          )
        }
      }

      item {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val twoCols = maxWidth >= 580.dp
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (twoCols) {
              uiState.futureFeatures.chunked(2).forEach { pair ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  pair.forEach { feature ->
                    FutureFeatureCard(
                      feature = feature,
                      onClick = { onInspectLockedFeature(feature) },
                      modifier = Modifier.weight(1f),
                    )
                  }
                  if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
              }
            } else {
              uiState.futureFeatures.forEach { feature ->
                FutureFeatureCard(
                  feature = feature,
                  onClick = { onInspectLockedFeature(feature) },
                  modifier = Modifier.fillMaxWidth(),
                )
              }
            }
          }
        }
      }

      item {
        Button(
          onClick = onNotifyMeClick,
          modifier =
            Modifier.fillMaxWidth()
              .height(54.dp)
              .minimumInteractiveComponentSize()
              .testTag("store_preview_notify_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
        ) {
          Icon(
            imageVector = Icons.Outlined.NotificationsActive,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text =
              if (uiState.registration.isSubscribed) "Manage Launch Notification"
              else "Get Notified When Store Opens",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun LockedQuickTile(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
    border = BorderStroke(1.dp, HairlineBorder),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = "$title disabled until launch",
        tint = MutedSlate,
        modifier = Modifier.size(22.dp),
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = ObsidianInk,
      )
      Icon(
        imageVector = Icons.Outlined.Lock,
        contentDescription = null,
        tint = MutedSlate.copy(alpha = 0.65f),
        modifier = Modifier.size(13.dp),
      )
    }
  }
}

@Composable
private fun FutureFeatureCard(
  feature: FutureFeature,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.testTag("future_feature_${feature.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    border = BorderStroke(1.dp, HairlineBorder),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = StoneContainer,
        modifier = Modifier.size(44.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = feature.icon,
            contentDescription = feature.title,
            tint = MutedSlate,
            modifier = Modifier.size(22.dp),
          )
        }
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = feature.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = ObsidianInk,
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = AlabasterSurface,
            border = BorderStroke(1.dp, HairlineBorder),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MutedSlate,
                modifier = Modifier.size(11.dp),
              )
              Text(
                text = "AT LAUNCH",
                style = MaterialTheme.typography.labelSmall,
                color = MutedSlate,
              )
            }
          }
        }
        Text(
          text = feature.description,
          style = MaterialTheme.typography.bodySmall,
          color = MutedSlate,
        )
      }
    }
  }
}
