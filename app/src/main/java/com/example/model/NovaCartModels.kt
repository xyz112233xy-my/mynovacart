package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberSoft
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.IndigoSoft
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealSoft

data class LaunchCategory(
  val id: String,
  val title: String,
  val subtitle: String,
  val launchExpectation: String,
  val shopifyCollectionHandle: String,
  val icon: ImageVector,
  val accentColor: Color,
  val containerColor: Color,
)

data class FutureFeature(
  val id: String,
  val title: String,
  val description: String,
  val shopifyBridge: String,
  val icon: ImageVector,
  val isEnabled: Boolean = true,
)

data class ShopVariant(
  val id: Long,
  val title: String,
  val priceAmount: Double,
  val compareAtPriceAmount: Double? = null,
  val available: Boolean = true,
)

data class ShopProduct(
  val id: Long,
  val title: String,
  val handle: String,
  val description: String,
  val vendor: String,
  val productType: String,
  val categoryId: String?,
  val imageUrl: String?,
  val imageUrls: List<String>,
  val variants: List<ShopVariant>,
  val available: Boolean,
) {
  val primaryVariant: ShopVariant?
    get() = variants.firstOrNull { it.available } ?: variants.firstOrNull()

  val displayPrice: Double
    get() = primaryVariant?.priceAmount ?: 0.0

  val compareAtPrice: Double?
    get() = primaryVariant?.compareAtPriceAmount
}

data class CartItem(
  val productId: Long,
  val variantId: Long,
  val title: String,
  val variantTitle: String,
  val priceAmount: Double,
  val imageUrl: String?,
  val quantity: Int,
)

data class OrderRecord(
  val id: String,
  val checkoutUrl: String,
  val itemCount: Int,
  val totalAmount: Double,
  val summary: String,
  val createdAtEpochMs: Long,
  val status: String,
)

enum class ProductSortOption(val label: String) {
  FEATURED("Featured"),
  PRICE_LOW_TO_HIGH("Price: Low to High"),
  PRICE_HIGH_TO_LOW("Price: High to Low"),
  NAME_A_TO_Z("Name: A to Z"),
}

data class NotifyMeRegistration(
  val email: String = "",
  val fullName: String = "",
  val interestedCategoryIds: Set<String> = emptySet(),
  val isSubscribed: Boolean = false,
  val registeredAtEpochMs: Long? = null,
)

data class StoreLaunchStatus(
  val storeUrl: String = "https://mynovacart.in",
  val isStoreLaunched: Boolean = true,
  val isChecking: Boolean = false,
  val isWebsiteReachable: Boolean? = null,
  val isPasswordProtected: Boolean = false,
  val syncedProductCount: Int = 0,
  val syncedCollectionCount: Int = 0,
  val statusHeadline: String = "Connected to mynovacart.in",
  val statusDetail: String = "Syncing live products and collections from your Shopify store.",
  val errorMessage: String? = null,
  val lastCheckedEpochMs: Long? = null,
  val shopifyStorefrontConfigured: Boolean = true,
)

data class ShopifyIntegrationCapability(
  val title: String,
  val detail: String,
)

object NovaCartCatalogBlueprint {
  const val OFFICIAL_WEBSITE_URL = "https://mynovacart.in"
  const val OFFICIAL_DOMAIN_LABEL = "mynovacart.in"

  val launchCategories: List<LaunchCategory> =
    listOf(
      LaunchCategory(
        id = "home_decor",
        title = "Home Decor",
        subtitle = "Thoughtful accents, lighting, and modern living essentials.",
        launchExpectation =
          "Curated ceramics, warm tabletop accents, wall styling, and living room pieces synced directly from Shopify.",
        shopifyCollectionHandle = "home-decor",
        icon = Icons.Outlined.Home,
        accentColor = NovaCoral,
        containerColor = NovaCoralSoft,
      ),
      LaunchCategory(
        id = "kitchen_accessories",
        title = "Kitchen Accessories",
        subtitle = "Smart cookware, storage, and everyday culinary tools.",
        launchExpectation =
          "Organized pantry containers, prep tools, kettleware, and practical dining essentials.",
        shopifyCollectionHandle = "kitchen-accessories",
        icon = Icons.Outlined.Kitchen,
        accentColor = AmberAccent,
        containerColor = AmberSoft,
      ),
      LaunchCategory(
        id = "beauty_care",
        title = "Beauty Care",
        subtitle = "Daily skincare, grooming, and self-care essentials.",
        launchExpectation =
          "Authentic self-care tools, organizers, and daily beauty staples with live inventory.",
        shopifyCollectionHandle = "beauty-care",
        icon = Icons.Outlined.Spa,
        accentColor = RoseAccent,
        containerColor = RoseSoft,
      ),
      LaunchCategory(
        id = "daily_life_gadgets",
        title = "Daily Life Gadgets",
        subtitle = "Practical tech and clever accessories for everyday flow.",
        launchExpectation =
          "Desk chargers, compact utility devices, and smart lifestyle gear for daily routines.",
        shopifyCollectionHandle = "daily-life-gadgets",
        icon = Icons.Outlined.DevicesOther,
        accentColor = IndigoAccent,
        containerColor = IndigoSoft,
      ),
      LaunchCategory(
        id = "kids",
        title = "Kids",
        subtitle = "Playful, safe, and practical essentials for growing kids.",
        launchExpectation =
          "Everyday organizers, creative activity gear, and family-friendly essentials.",
        shopifyCollectionHandle = "kids",
        icon = Icons.Outlined.ChildCare,
        accentColor = TealAccent,
        containerColor = TealSoft,
      ),
    )

  val futureShoppingFeatures: List<FutureFeature> =
    listOf(
      FutureFeature(
        id = "product_browsing",
        title = "Product Browsing",
        description = "Explore live collections and products synced from Shopify.",
        shopifyBridge = "Shopify /products.json & /collections.json",
        icon = Icons.Outlined.ShoppingBag,
        isEnabled = true,
      ),
      FutureFeature(
        id = "product_search",
        title = "Product Search",
        description = "Instant keyword, category, and price sorting across live inventory.",
        shopifyBridge = "In-App Search & Filter Engine",
        icon = Icons.Outlined.Search,
        isEnabled = true,
      ),
      FutureFeature(
        id = "shopping_cart",
        title = "Shopping Cart",
        description = "Add variants, adjust quantities, and persist cart items locally.",
        shopifyBridge = "Shopify Cart Permalink Bridge",
        icon = Icons.Outlined.ShoppingCart,
        isEnabled = true,
      ),
      FutureFeature(
        id = "wishlist",
        title = "Wishlist",
        description = "Save favourite items for quick access and one-tap cart addition.",
        shopifyBridge = "Persistent Device Wishlist",
        icon = Icons.Outlined.FavoriteBorder,
        isEnabled = true,
      ),
      FutureFeature(
        id = "secure_checkout",
        title = "Secure Checkout",
        description = "Encrypted checkout session directly on your Shopify store.",
        shopifyBridge = "Shopify Hosted Cart & Checkout",
        icon = Icons.Outlined.Lock,
        isEnabled = true,
      ),
      FutureFeature(
        id = "upi_online_payments",
        title = "UPI & Online Payments",
        description = "Supports UPI apps, cards, and net banking configured in Shopify.",
        shopifyBridge = "In-App WebView + UPI Intent Routing",
        icon = Icons.Outlined.QrCode2,
        isEnabled = true,
      ),
      FutureFeature(
        id = "cash_on_delivery",
        title = "Cash on Delivery",
        description = "Uses your existing Shopify COD rules and shipping zones.",
        shopifyBridge = "Shopify Checkout Payment Rules",
        icon = Icons.Outlined.Payments,
        isEnabled = true,
      ),
      FutureFeature(
        id = "customer_accounts",
        title = "Customer Accounts",
        description = "Sign in to your Shopify customer account directly inside the app.",
        shopifyBridge = "Shopify /account Customer Portal",
        icon = Icons.Outlined.AccountCircle,
        isEnabled = true,
      ),
      FutureFeature(
        id = "order_tracking",
        title = "Order Tracking",
        description = "Track live Shopify order status and shipping milestones.",
        shopifyBridge = "Shopify /account/orders Portal",
        icon = Icons.Outlined.LocalShipping,
        isEnabled = true,
      ),
      FutureFeature(
        id = "order_history",
        title = "Order History",
        description = "View past checkouts initiated from the app and your Shopify orders.",
        shopifyBridge = "Local Order Log + Shopify Orders",
        icon = Icons.Outlined.History,
        isEnabled = true,
      ),
    )

  val shopifySyncCapabilities: List<ShopifyIntegrationCapability> =
    listOf(
      ShopifyIntegrationCapability(
        title = "Automatic Product Sync",
        detail =
          "Products and collections sync directly from your Shopify store URL with zero duplicate database.",
      ),
      ShopifyIntegrationCapability(
        title = "Live Prices & Inventory",
        detail =
          "Variant prices, compare-at prices, and stock availability update straight from Shopify.",
      ),
      ShopifyIntegrationCapability(
        title = "Orders Appear in Shopify",
        detail =
          "Cart checkouts route via Shopify cart permalinks so every order goes directly into your Shopify Admin.",
      ),
      ShopifyIntegrationCapability(
        title = "Existing Payment Settings Respected",
        detail =
          "UPI apps, cards, wallets, and Cash on Delivery configured in Shopify work automatically.",
      ),
    )
}
