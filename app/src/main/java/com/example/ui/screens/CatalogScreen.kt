package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.model.ProductSortOption
import com.example.model.ShopProduct
import com.example.ui.components.ShopProductCard
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun CatalogScreen(
  uiState: NovaCartUiState,
  onSearchQueryChanged: (String) -> Unit,
  onSelectCategory: (String?) -> Unit,
  onToggleWishlistFilter: () -> Unit,
  onSelectSort: (ProductSortOption) -> Unit,
  onProductClick: (ShopProduct) -> Unit,
  onToggleWishlist: (Long) -> Unit,
  onQuickAddProduct: (ShopProduct) -> Unit,
  onSyncStore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val filtered = uiState.filteredProducts

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 860.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = "Shop Catalog",
              style = MaterialTheme.typography.headlineMedium,
              color = ObsidianInk,
            )
            Text(
              text = "${filtered.size} products • ${uiState.storeStatus.storeUrl}",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )
          }
          IconButton(onClick = onSyncStore, modifier = Modifier.testTag("catalog_sync_button")) {
            Icon(Icons.Outlined.Refresh, contentDescription = "Sync Shopify catalog", tint = ObsidianInk)
          }
        }
      }

      // Live Search Bar
      item {
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = onSearchQueryChanged,
          placeholder = { Text("Search products, categories, brands...") },
          leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
          trailingIcon = {
            if (uiState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChanged("") }) {
                Icon(Icons.Outlined.Close, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          shape = RoundedCornerShape(14.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ObsidianInk,
              unfocusedBorderColor = HairlineBorder,
              focusedContainerColor = AlabasterSurface,
              unfocusedContainerColor = AlabasterSurface,
            ),
          modifier = Modifier.fillMaxWidth().testTag("catalog_search_input"),
        )
      }

      // Category & Wishlist Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          FilterChip(
            selected = uiState.selectedCategoryId == null && !uiState.wishlistOnly,
            onClick = { onSelectCategory(null) },
            label = { Text("All") },
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = ObsidianInk,
                selectedLabelColor = PureWhite,
                containerColor = AlabasterSurface,
                labelColor = ObsidianInk,
              ),
          )

          FilterChip(
            selected = uiState.wishlistOnly,
            onClick = onToggleWishlistFilter,
            leadingIcon = {
              Icon(
                imageVector = if (uiState.wishlistOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
            },
            label = { Text("Wishlist (${uiState.wishlistIds.size})") },
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = NovaCoral,
                selectedLabelColor = PureWhite,
                selectedLeadingIconColor = PureWhite,
                containerColor = AlabasterSurface,
                labelColor = ObsidianInk,
              ),
            modifier = Modifier.testTag("catalog_wishlist_chip"),
          )

          uiState.categories.forEach { cat ->
            val selected = uiState.selectedCategoryId == cat.id
            FilterChip(
              selected = selected,
              onClick = { onSelectCategory(if (selected) null else cat.id) },
              label = { Text(cat.title) },
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

      // Sort Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("Sort:", style = MaterialTheme.typography.labelMedium, color = MutedSlate)
          ProductSortOption.entries.forEach { option ->
            val selected = uiState.sortOption == option
            FilterChip(
              selected = selected,
              onClick = { onSelectSort(option) },
              label = { Text(option.label) },
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ObsidianInk,
                  selectedLabelColor = PureWhite,
                  containerColor = PureWhite,
                  labelColor = ObsidianInk,
                ),
            )
          }
        }
      }

      if (filtered.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(
                imageVector = Icons.Outlined.ShoppingBag,
                contentDescription = null,
                tint = MutedSlate,
                modifier = Modifier.size(36.dp),
              )
              Text(
                text =
                  when {
                    uiState.products.isEmpty() -> "Store catalog updating"
                    uiState.wishlistOnly -> "Your wishlist is empty"
                    else -> "No matching products found"
                  },
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
              )
              Text(
                text =
                  when {
                    uiState.products.isEmpty() ->
                      "Published items from mynovacart.in will appear here automatically."
                    uiState.wishlistOnly -> "Tap the heart icon on any product to save it here."
                    else -> "Try clearing your search or switching departments."
                  },
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
              )
              if (uiState.products.isEmpty()) {
                Button(
                  onClick = onSyncStore,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
                ) {
                  Text("Refresh Catalog")
                }
              }
            }
          }
        }
      } else {
        item {
          BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columns = if (maxWidth >= 600.dp) 3 else 2
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              filtered.chunked(columns).forEach { rowProducts ->
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

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}
