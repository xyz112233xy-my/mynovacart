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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.CartItem
import com.example.ui.components.formatInrPrice
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun CartScreen(
  uiState: NovaCartUiState,
  onUpdateQuantity: (Long, Int) -> Unit,
  onRemoveItem: (Long) -> Unit,
  onProceedToCheckout: () -> Unit,
  onBrowseProducts: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.fillMaxSize().widthIn(max = 820.dp)) {
      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        item {
          Column {
            Text(
              text = "Shopping Cart",
              style = MaterialTheme.typography.headlineMedium,
              color = ObsidianInk,
            )
            Text(
              text =
                if (uiState.cartItems.isEmpty()) "Ready for Shopify checkout"
                else "${uiState.cartItemCount} item(s) • Synced for ${uiState.storeStatus.storeUrl}",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )
          }
        }

        if (uiState.cartItems.isEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.ShoppingCart,
                  contentDescription = null,
                  tint = MutedSlate,
                  modifier = Modifier.size(44.dp),
                )
                Text(
                  text = "Your cart is empty",
                  style = MaterialTheme.typography.titleLarge,
                  color = ObsidianInk,
                )
                Text(
                  text =
                    "Browse products synced from Shopify and tap Add to build your order. Checkout supports UPI, online payments, and Cash on Delivery.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MutedSlate,
                )
                Button(
                  onClick = onBrowseProducts,
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
                  modifier = Modifier.testTag("empty_cart_browse_button"),
                ) {
                  Text("Browse Products", style = MaterialTheme.typography.labelLarge)
                }
              }
            }
          }
        } else {
          items(uiState.cartItems, key = { it.variantId }) { item ->
            CartItemRow(
              item = item,
              onDecrease = { onUpdateQuantity(item.variantId, -1) },
              onIncrease = { onUpdateQuantity(item.variantId, 1) },
              onRemove = { onRemoveItem(item.variantId) },
            )
          }

          item {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = EmeraldSoft,
              border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.2f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(Icons.Outlined.QrCode2, contentDescription = null, tint = EmeraldLive)
                Icon(Icons.Outlined.Payments, contentDescription = null, tint = EmeraldLive)
                Text(
                  text =
                    "Shopify Checkout supports UPI, cards, net banking & Cash on Delivery per your store settings.",
                  style = MaterialTheme.typography.bodySmall,
                  color = ObsidianInk,
                )
              }
            }
          }
        }
      }

      if (uiState.cartItems.isNotEmpty()) {
        HorizontalDivider(color = HairlineBorder)
        Column(
          modifier = Modifier.fillMaxWidth().background(PureWhite).padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Subtotal", style = MaterialTheme.typography.titleMedium, color = MutedSlate)
            Text(
              text = formatInrPrice(uiState.cartSubtotal),
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = ObsidianInk,
            )
          }

          Button(
            onClick = onProceedToCheckout,
            modifier = Modifier.fillMaxWidth().height(54.dp).testTag("proceed_to_checkout_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
          ) {
            Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Checkout on Shopify", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }
  }
}

@Composable
private fun CartItemRow(
  item: CartItem,
  onDecrease: () -> Unit,
  onIncrease: () -> Unit,
  onRemove: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
    border = BorderStroke(1.dp, HairlineBorder),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(PureWhite),
        contentAlignment = Alignment.Center,
      ) {
        if (!item.imageUrl.isNullOrBlank()) {
          AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        } else {
          Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = MutedSlate)
        }
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.titleSmall,
          color = ObsidianInk,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        if (item.variantTitle != "Default Title") {
          Text(text = item.variantTitle, style = MaterialTheme.typography.bodySmall, color = MutedSlate)
        }
        Text(
          text = formatInrPrice(item.priceAmount * item.quantity),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = ObsidianInk,
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
          Icon(
            Icons.Outlined.DeleteOutline,
            contentDescription = "Remove item",
            tint = MutedSlate,
            modifier = Modifier.size(18.dp),
          )
        }
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = PureWhite,
          border = BorderStroke(1.dp, HairlineBorder),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Outlined.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }
            Text(
              text = item.quantity.toString(),
              style = MaterialTheme.typography.labelLarge,
              color = ObsidianInk,
              modifier = Modifier.padding(horizontal = 6.dp),
            )
            IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
              Icon(Icons.Outlined.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}
