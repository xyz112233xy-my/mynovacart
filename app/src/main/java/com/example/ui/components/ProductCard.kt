package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.ShopProduct
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite

@Composable
fun ShopProductCard(
  product: ShopProduct,
  isWishlisted: Boolean,
  onProductClick: () -> Unit,
  onToggleWishlist: () -> Unit,
  onQuickAdd: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onProductClick,
    modifier = modifier.testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    border = BorderStroke(1.dp, HairlineBorder),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(AlabasterSurface),
        contentAlignment = Alignment.Center,
      ) {
        if (!product.imageUrl.isNullOrBlank()) {
          AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
        } else {
          Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = product.title,
            tint = MutedSlate,
            modifier = Modifier.size(36.dp),
          )
        }

        Surface(
          shape = CircleShape,
          color = PureWhite.copy(alpha = 0.92f),
          modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(36.dp),
        ) {
          IconButton(
            onClick = onToggleWishlist,
            modifier = Modifier.testTag("wishlist_toggle_${product.id}"),
          ) {
            Icon(
              imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = if (isWishlisted) "Remove from wishlist" else "Save to wishlist",
              tint = if (isWishlisted) NovaCoral else ObsidianInk,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }

      Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = product.vendor,
          style = MaterialTheme.typography.labelSmall,
          color = MutedSlate,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = product.title,
          style = MaterialTheme.typography.titleSmall,
          color = ObsidianInk,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          minLines = 2,
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = formatInrPrice(product.displayPrice),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ObsidianInk,
          )
          val compare = product.compareAtPrice
          if (compare != null && compare > product.displayPrice) {
            Text(
              text = formatInrPrice(compare),
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
              textDecoration = TextDecoration.LineThrough,
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = onQuickAdd,
          enabled = product.available,
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          shape = RoundedCornerShape(10.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = ObsidianInk,
              contentColor = PureWhite,
            ),
          modifier = Modifier.fillMaxWidth().height(38.dp).testTag("add_cart_${product.id}"),
        ) {
          Icon(
            imageVector = Icons.Outlined.AddShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
          )
          Text(
            text = if (product.available) " Add" else "Sold Out",
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }
    }
  }
}
