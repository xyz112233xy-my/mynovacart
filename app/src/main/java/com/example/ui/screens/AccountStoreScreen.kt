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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.formatInrPrice
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AccountStoreScreen(
  uiState: NovaCartUiState,
  onStoreUrlChanged: (String) -> Unit,
  onSaveAndSyncStore: () -> Unit,
  onOpenCustomerAccount: () -> Unit,
  onOpenOrdersPortal: () -> Unit,
  onOpenWebsitePortal: () -> Unit,
  onReopenOrderUrl: (String, String) -> Unit,
  onShareApp: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val status = uiState.storeStatus

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Account & Shopify Store",
            style = MaterialTheme.typography.headlineMedium,
            color = ObsidianInk,
          )
          Text(
            text = "Customer login, order tracking & live Shopify sync",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedSlate,
          )
        }
      }

      // Customer Account & Order Tracking Actions
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = ObsidianInk),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = "Customer Portal",
              style = MaterialTheme.typography.titleLarge,
              color = PureWhite,
            )
            Text(
              text =
                "Sign in to your Shopify customer account to view saved addresses, track shipments, and inspect full order history.",
              style = MaterialTheme.typography.bodySmall,
              color = PureWhite.copy(alpha = 0.78f),
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = onOpenCustomerAccount,
                modifier = Modifier.weight(1f).height(48.dp).testTag("open_customer_account_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = ObsidianInk),
              ) {
                Icon(Icons.Outlined.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sign In", style = MaterialTheme.typography.labelLarge)
              }
              Button(
                onClick = onOpenOrdersPortal,
                modifier = Modifier.weight(1f).height(48.dp).testTag("open_orders_portal_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
              ) {
                Icon(Icons.Outlined.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Track Orders", style = MaterialTheme.typography.labelLarge)
              }
            }
          }
        }
      }

      // Recent App Orders
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Recent App Orders (${uiState.orderHistory.size})",
            style = MaterialTheme.typography.titleLarge,
            color = ObsidianInk,
          )
          if (uiState.orderHistory.isEmpty()) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = AlabasterSurface,
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text =
                  "Orders placed through the app cart will appear here automatically alongside your Shopify order history.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
                modifier = Modifier.padding(16.dp),
              )
            }
          } else {
            uiState.orderHistory.forEach { order ->
              Card(
                onClick = { onReopenOrderUrl(order.checkoutUrl, "Order ${order.id}") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
                border = BorderStroke(1.dp, HairlineBorder),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Text(
                      text = order.id,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = ObsidianInk,
                    )
                    Text(
                      text = formatInrPrice(order.totalAmount),
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = EmeraldLive,
                    )
                  }
                  Text(order.summary, style = MaterialTheme.typography.bodySmall, color = MutedSlate)
                  Text(
                    text =
                      "${order.status} • ${SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(order.createdAtEpochMs))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate,
                  )
                }
              }
            }
          }
        }
      }

      // Connect Shopify Storefront Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(Icons.Outlined.CloudSync, contentDescription = null, tint = NovaCoral)
                Text(
                  text = "Shopify Website Connection",
                  style = MaterialTheme.typography.titleMedium,
                  color = ObsidianInk,
                )
              }
              Surface(
                shape = CircleShape,
                color = if (status.syncedProductCount > 0) EmeraldSoft else NovaCoralSoft,
              ) {
                Text(
                  text =
                    when {
                      status.isChecking -> "SYNCING"
                      status.syncedProductCount > 0 -> "LIVE"
                      status.isPasswordProtected -> "PASSWORD ON"
                      else -> "CONNECTED"
                    },
                  style = MaterialTheme.typography.labelSmall,
                  color = if (status.syncedProductCount > 0) EmeraldLive else NovaCoral,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }
            }

            Text(
              text = status.statusDetail,
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )

            OutlinedTextField(
              value = uiState.storeUrlInput,
              onValueChange = onStoreUrlChanged,
              label = { Text("Shopify Store URL") },
              placeholder = { Text("https://mynovacart.in") },
              singleLine = true,
              keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
              shape = RoundedCornerShape(12.dp),
              colors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ObsidianInk,
                  unfocusedBorderColor = HairlineBorder,
                  focusedContainerColor = PureWhite,
                  unfocusedContainerColor = PureWhite,
                ),
              modifier = Modifier.fillMaxWidth().testTag("shopify_url_input"),
            )

            Button(
              onClick = onSaveAndSyncStore,
              enabled = !status.isChecking,
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sync_shopify_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
            ) {
              if (status.isChecking) {
                CircularProgressIndicator(color = PureWhite, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Syncing Products...")
              } else {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Sync Shopify Catalog", style = MaterialTheme.typography.labelLarge)
              }
            }
          }
        }
      }

      // All 10 Unlocked Capabilities Status
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Unlocked Store Capabilities (${uiState.futureFeatures.size}/${uiState.futureFeatures.size})",
            style = MaterialTheme.typography.titleMedium,
            color = ObsidianInk,
          )
          uiState.futureFeatures.forEach { feature ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = PureWhite,
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.CheckCircle,
                  contentDescription = "Unlocked",
                  tint = EmeraldLive,
                  modifier = Modifier.size(18.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(feature.title, style = MaterialTheme.typography.titleSmall, color = ObsidianInk)
                  Text(feature.description, style = MaterialTheme.typography.bodySmall, color = MutedSlate)
                }
              }
            }
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onOpenWebsitePortal,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
          ) {
            Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(NovaCartCatalogBlueprint.OFFICIAL_DOMAIN_LABEL)
          }
          OutlinedButton(
            onClick = onShareApp,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
          ) {
            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share App")
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}
