package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LaunchCategory
import com.example.model.ShopProduct
import com.example.ui.components.NovaCartWordmarkHeader
import com.example.ui.components.ShopProductCard
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
fun ShopHomeScreen(
  uiState: NovaCartUiState,
  onSearchBarClick: () -> Unit,
  onShopNowClick: () -> Unit,
  onCategoryClick: (LaunchCategory) -> Unit,
  onProductClick: (ShopProduct) -> Unit,
  onToggleWishlist: (Long) -> Unit,
  onQuickAddProduct: (ShopProduct) -> Unit,
  onRefreshStore: () -> Unit,
  onOpenWebsitePortal: () -> Unit,
  onOpenOrdersPortal: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val status = uiState.storeStatus

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 860.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      // Brand Header + Live Store Status Pill
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          NovaCartWordmarkHeader()
          Surface(
            shape = CircleShape,
            color = if (status.syncedProductCount > 0) EmeraldSoft else NovaCoralSoft,
            border =
              BorderStroke(
                1.dp,
                if (status.syncedProductCount > 0) EmeraldLive.copy(alpha = 0.25f)
                else NovaCoral.copy(alpha = 0.25f),
              ),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Box(
                modifier =
                  Modifier.size(7.dp)
                    .background(
                      if (status.syncedProductCount > 0) EmeraldLive else NovaCoral,
                      shape = CircleShape,
                    )
              )
              Text(
                text =
                  when {
                    status.isChecking -> "SYNCING..."
                    status.syncedProductCount > 0 -> "LIVE • ${status.syncedProductCount} ITEMS"
                    else -> "OFFICIAL STORE"
                  },
                style = MaterialTheme.typography.labelSmall,
                color = if (status.syncedProductCount > 0) EmeraldLive else NovaCoral,
              )
            }
          }
        }
      }

      // Unlocked Search Shortcut
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = AlabasterSurface,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier =
            Modifier.fillMaxWidth()
              .clickable(onClick = onSearchBarClick)
              .testTag("home_search_bar"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = "Search NOVA CART products",
              tint = ObsidianInk,
            )
            Text(
              text = "Search Home Decor, Kitchen, Beauty, Gadgets, Kids...",
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate,
              modifier = Modifier.weight(1f),
            )
          }
        }
      }

      // Hero Shopping Banner
      item {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(AlabasterSurface),
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_hero_banner),
            contentDescription = "NOVA CART curated lifestyle shopping banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
          )
          Box(
            modifier =
              Modifier.matchParentSize()
                .background(
                  Brush.verticalGradient(
                    colors =
                      listOf(
                        Color.Transparent,
                        ObsidianInk.copy(alpha = 0.15f),
                        ObsidianInk.copy(alpha = 0.72f),
                      )
                  )
                )
          )

          Row(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "NOVA CART Store",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
              )
              Text(
                text = "Shop directly on mynovacart.in",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.85f),
              )
            }
            Button(
              onClick = onShopNowClick,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
              modifier = Modifier.testTag("home_shop_now_button"),
            ) {
              Text("Shop All", style = MaterialTheme.typography.labelLarge)
            }
          }
        }
      }

      // Shop by Category (Horizontal Row)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Shop by Department",
              style = MaterialTheme.typography.titleLarge,
              color = ObsidianInk,
            )
            Text(
              text = "See all",
              style = MaterialTheme.typography.labelLarge,
              color = NovaCoral,
              modifier = Modifier.clickable(onClick = onShopNowClick).padding(4.dp),
            )
          }

          LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.categories, key = { it.id }) { category ->
              Card(
                onClick = { onCategoryClick(category) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
                border = BorderStroke(1.dp, HairlineBorder),
                modifier = Modifier.width(136.dp).testTag("home_category_${category.id}"),
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = category.containerColor,
                    modifier = Modifier.size(44.dp),
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = category.accentColor,
                      )
                    }
                  }
                  Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = ObsidianInk,
                    maxLines = 2,
                  )
                }
              }
            }
          }
        }
      }

      // Featured Store Products
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Featured Arrivals",
              style = MaterialTheme.typography.titleLarge,
              color = ObsidianInk,
            )
            OutlinedButton(
              onClick = onRefreshStore,
              enabled = !status.isChecking,
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, HairlineBorder),
            ) {
              if (status.isChecking) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
              } else {
                Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", modifier = Modifier.size(15.dp))
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text("Refresh", style = MaterialTheme.typography.labelMedium, color = ObsidianInk)
            }
          }

          if (uiState.products.isEmpty()) {
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = NovaCoral)
                  Text(
                    text =
                      if (status.isChecking) "Loading NOVA CART catalog..."
                      else "Store catalog updating",
                    style = MaterialTheme.typography.titleMedium,
                    color = ObsidianInk,
                  )
                }
                Text(
                  text =
                    "New products from mynovacart.in appear here automatically as soon as they are published.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MutedSlate,
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Button(
                    onClick = onOpenWebsitePortal,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
                  ) {
                    Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Visit Store", style = MaterialTheme.typography.labelLarge)
                  }
                  OutlinedButton(
                    onClick = onRefreshStore,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ObsidianInk),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
                  ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refresh", style = MaterialTheme.typography.labelLarge)
                  }
                }
              }
            }
          } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
              val columns = if (maxWidth >= 600.dp) 3 else 2
              val previewProducts = uiState.products.take(6)
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                previewProducts.chunked(columns).forEach { rowProducts ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                  ) {
                    rowProducts.forEach { product ->
                      ShopProductCard(
                        product = product,
                        isWishlisted = uiState.wishlistIds.contains(product.id.toString()),
                        onProductClick = { onProductClick(product) },
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        onQuickAdd = { onQuickAddProduct(product) },
                        modifier = Modifier.weight(1f),
                      )
                    }
                    repeat(columns - rowProducts.size) {
                      Spacer(modifier = Modifier.weight(1f))
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Quick Order Tracking & Storefront Portal Card
      item {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = ObsidianInk,
          modifier =
            Modifier.fillMaxWidth()
              .clickable(onClick = onOpenOrdersPortal)
              .minimumInteractiveComponentSize()
              .testTag("home_track_orders_card"),
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = "Track orders",
                tint = NovaCoral,
              )
              Column {
                Text(
                  text = "Track Orders & Shopify Account",
                  style = MaterialTheme.typography.titleSmall,
                  color = PureWhite,
                )
                Text(
                  text = "UPI, Online Payments & Cash on Delivery orders synced with Shopify",
                  style = MaterialTheme.typography.bodySmall,
                  color = PureWhite.copy(alpha = 0.75f),
                )
              }
            }
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
              contentDescription = null,
              tint = PureWhite,
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(10.dp)) }
    }
  }
}
