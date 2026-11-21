package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.formatInrPrice
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountStoreScreen(
  uiState: NovaCartUiState,
  onOpenCustomerAccount: () -> Unit,
  onOpenOrdersPortal: () -> Unit,
  onOpenWebsitePortal: () -> Unit,
  onReopenOrderUrl: (String, String) -> Unit,
  onShareApp: () -> Unit,
  onOpenOwnerStudio: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var ownerTapCount by remember { mutableIntStateOf(0) }

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp).testTag("account_lazy_list"),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "My Account",
            style = MaterialTheme.typography.headlineMedium,
            color = ObsidianInk,
          )
          Text(
            text = "Orders, account sign-in & NOVA CART store services",
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
                "Sign in to your NOVA CART account to view saved addresses, track shipments, and inspect your complete order history.",
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
            text = "My Orders (${uiState.orderHistory.size})",
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
                  "Orders you place from your cart will appear here automatically for quick receipt & shipment access.",
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

      // Customer Assurance Card
      item {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = AlabasterSurface,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = EmeraldLive)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "100% Official NOVA CART Checkout",
                style = MaterialTheme.typography.titleSmall,
                color = ObsidianInk,
              )
              Text(
                text = "Supports UPI, Cards, Net Banking & Cash on Delivery across India.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
              )
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

      // Subtle footer for Owner Studio access (hidden from normal shopping workflow: long-press or 5 taps)
      item {
        Text(
          text = "NOVA CART • v1.0",
          style = MaterialTheme.typography.labelSmall,
          color = MutedSlate.copy(alpha = 0.65f),
          textAlign = TextAlign.Center,
          modifier =
            Modifier.fillMaxWidth()
              .combinedClickable(
                onClick = {
                  ownerTapCount += 1
                  if (ownerTapCount >= 5) {
                    ownerTapCount = 0
                    onOpenOwnerStudio()
                  }
                },
                onLongClick = {
                  ownerTapCount = 0
                  onOpenOwnerStudio()
                },
              )
              .padding(vertical = 12.dp)
              .testTag("account_version_footer"),
        )
      }
    }
  }
}
