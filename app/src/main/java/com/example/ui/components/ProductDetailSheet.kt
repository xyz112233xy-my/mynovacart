package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.ShopProduct
import com.example.model.ShopVariant
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailSheet(
  product: ShopProduct?,
  isWishlisted: Boolean,
  onToggleWishlist: (Long) -> Unit,
  onAddToCart: (ShopProduct, ShopVariant, Int) -> Unit,
  onBuyNow: (ShopProduct, ShopVariant, Int) -> Unit,
  onDismiss: () -> Unit,
) {
  if (product == null) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedVariant by remember(product.id) {
    mutableStateOf(product.primaryVariant ?: product.variants.firstOrNull())
  }
  var quantity by remember(product.id) { mutableIntStateOf(1) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    contentColor = ObsidianInk,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp)
          .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(20.dp))
            .background(AlabasterSurface),
        contentAlignment = Alignment.Center,
      ) {
        if (!product.imageUrl.isNullOrBlank()) {
          AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
          )
        } else {
          Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = product.title,
            tint = MutedSlate,
            modifier = Modifier.size(48.dp),
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = product.vendor.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = NovaCoral,
          )
          Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            color = ObsidianInk,
          )
        }

        IconButton(
          onClick = { onToggleWishlist(product.id) },
          modifier = Modifier.testTag("detail_wishlist_button"),
        ) {
          Icon(
            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isWishlisted) "Remove from wishlist" else "Save to wishlist",
            tint = if (isWishlisted) NovaCoral else ObsidianInk,
          )
        }
      }

      val activeVariant = selectedVariant
      if (activeVariant != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = formatInrPrice(activeVariant.priceAmount),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ObsidianInk,
          )
          val compareAt = activeVariant.compareAtPriceAmount
          if (compareAt != null && compareAt > activeVariant.priceAmount) {
            Text(
              text = formatInrPrice(compareAt),
              style = MaterialTheme.typography.bodyLarge,
              color = MutedSlate,
              textDecoration = TextDecoration.LineThrough,
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (activeVariant.available) EmeraldSoft else NovaCoralSoft,
          ) {
            Text(
              text = if (activeVariant.available) "In Stock" else "Out of Stock",
              style = MaterialTheme.typography.labelSmall,
              color = if (activeVariant.available) EmeraldLive else NovaCoral,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
          }
        }
      }

      if (product.variants.size > 1) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Options", style = MaterialTheme.typography.titleSmall, color = ObsidianInk)
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            product.variants.forEach { variant ->
              val selected = variant.id == selectedVariant?.id
              FilterChip(
                selected = selected,
                onClick = { selectedVariant = variant },
                label = { Text(variant.title) },
                colors =
                  FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ObsidianInk,
                    selectedLabelColor = PureWhite,
                    containerColor = AlabasterSurface,
                    labelColor = ObsidianInk,
                  ),
              )
            }
          }
        }
      }

      if (product.description.isNotBlank()) {
        Text(
          text = product.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MutedSlate,
        )
      }

      // Quantity Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Quantity", style = MaterialTheme.typography.titleSmall, color = ObsidianInk)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AlabasterSurface,
          border = BorderStroke(1.dp, HairlineBorder),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (quantity > 1) quantity -= 1 }) {
              Icon(Icons.Outlined.Remove, contentDescription = "Decrease quantity")
            }
            Text(
              text = quantity.toString(),
              style = MaterialTheme.typography.titleMedium,
              color = ObsidianInk,
              modifier = Modifier.padding(horizontal = 12.dp),
            )
            IconButton(onClick = { if (quantity < 20) quantity += 1 }) {
              Icon(Icons.Outlined.Add, contentDescription = "Increase quantity")
            }
          }
        }
      }

      val isAvailable = activeVariant?.available == true
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedButton(
          onClick = {
            if (activeVariant != null) {
              onAddToCart(product, activeVariant, quantity)
              onDismiss()
            }
          },
          enabled = isAvailable,
          modifier = Modifier.weight(1f).height(52.dp).testTag("detail_add_to_cart_button"),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.2.dp, ObsidianInk),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
        ) {
          Icon(Icons.Outlined.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add to Cart", style = MaterialTheme.typography.labelLarge)
        }

        Button(
          onClick = {
            if (activeVariant != null) {
              onBuyNow(product, activeVariant, quantity)
            }
          },
          enabled = isAvailable,
          modifier = Modifier.weight(1f).height(52.dp).testTag("detail_buy_now_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
        ) {
          Icon(Icons.Outlined.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Order Now", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}

fun formatInrPrice(amount: Double): String {
  return if (amount % 1.0 == 0.0) {
    "₹${amount.toInt()}"
  } else {
    String.format(Locale.US, "₹%.2f", amount)
  }
}
