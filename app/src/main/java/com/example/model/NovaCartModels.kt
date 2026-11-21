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
  val isEnabled: Boolean = false,
)

data class NotifyMeRegistration(
  val email: String = "",
  val fullName: String = "",
  val interestedCategoryIds: Set<String> = emptySet(),
  val isSubscribed: Boolean = false,
  val registeredAtEpochMs: Long? = null,
)

data class StoreLaunchStatus(
  val storeUrl: String = "https://mynovacart.in",
  val isStoreLaunched: Boolean = false,
  val isChecking: Boolean = false,
  val isWebsiteReachable: Boolean? = null,
  val statusHeadline: String = "Coming Soon — Pre-Launch Mode",
  val statusDetail: String =
    "Official website mynovacart.in is preparing for launch. Shopping features remain disabled until store opening.",
  val lastCheckedEpochMs: Long? = null,
  val shopifyStorefrontConfigured: Boolean = false,
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
          "Curated ceramics, warm tabletop accents, wall styling, and living room pieces syncing directly from our Shopify catalog at launch.",
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
          "Organized pantry containers, prep tools, kettleware, and practical dining essentials prepared for day-one availability.",
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
          "Authentic self-care tools, organizers, and daily beauty staples with live inventory tracking via Shopify.",
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
          "Desk chargers, compact utility devices, and smart lifestyle gear designed to simplify daily routines.",
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
          "Everyday organizers, creative activity gear, and family-friendly essentials curated for comfort and safety.",
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
        description = "Explore live collections across all five NOVA CART departments.",
        shopifyBridge = "Shopify Storefront Collections & Products API",
        icon = Icons.Outlined.ShoppingBag,
        isEnabled = false,
      ),
      FutureFeature(
        id = "product_search",
        title = "Product Search",
        description = "Instant keyword and category search across real-time inventory.",
        shopifyBridge = "Shopify Predictive & Catalog Query",
        icon = Icons.Outlined.Search,
        isEnabled = false,
      ),
      FutureFeature(
        id = "shopping_cart",
        title = "Shopping Cart",
        description = "Add items, adjust quantities, and sync cart state seamlessly.",
        shopifyBridge = "Shopify Cart Mutation API",
        icon = Icons.Outlined.ShoppingCart,
        isEnabled = false,
      ),
      FutureFeature(
        id = "wishlist",
        title = "Wishlist",
        description = "Save favourite items for later and track restocks.",
        shopifyBridge = "Synced Customer Saved List",
        icon = Icons.Outlined.FavoriteBorder,
        isEnabled = false,
      ),
      FutureFeature(
        id = "secure_checkout",
        title = "Secure Checkout",
        description = "Encrypted checkout session powered directly by mynovacart.in.",
        shopifyBridge = "Shopify Web & Hosted Checkout",
        icon = Icons.Outlined.Lock,
        isEnabled = false,
      ),
      FutureFeature(
        id = "upi_online_payments",
        title = "UPI & Online Payments",
        description = "Pay effortlessly via UPI, cards, and net banking configured in Shopify.",
        shopifyBridge = "Shopify Payment Gateway Settings",
        icon = Icons.Outlined.QrCode2,
        isEnabled = false,
      ),
      FutureFeature(
        id = "cash_on_delivery",
        title = "Cash on Delivery",
        description = "Pay on doorstep delivery where enabled by your Shopify shipping rules.",
        shopifyBridge = "Shopify COD Eligibility & Checkout",
        icon = Icons.Outlined.Payments,
        isEnabled = false,
      ),
      FutureFeature(
        id = "customer_accounts",
        title = "Customer Accounts",
        description = "Manage saved delivery addresses, profile details, and preferences.",
        shopifyBridge = "Shopify Customer Account API",
        icon = Icons.Outlined.AccountCircle,
        isEnabled = false,
      ),
      FutureFeature(
        id = "order_tracking",
        title = "Order Tracking",
        description = "Follow shipment milestones and carrier tracking updates in real time.",
        shopifyBridge = "Shopify Fulfillment Tracking",
        icon = Icons.Outlined.LocalShipping,
        isEnabled = false,
      ),
      FutureFeature(
        id = "order_history",
        title = "Order History",
        description = "View past orders, receipts, and fulfillment summaries in one place.",
        shopifyBridge = "Shopify Customer Orders Query",
        icon = Icons.Outlined.History,
        isEnabled = false,
      ),
    )

  val shopifySyncCapabilities: List<ShopifyIntegrationCapability> =
    listOf(
      ShopifyIntegrationCapability(
        title = "Automatic Product Sync",
        detail =
          "Products and all five launch collections pull directly from mynovacart.in with zero duplicate catalog.",
      ),
      ShopifyIntegrationCapability(
        title = "Live Prices & Inventory",
        detail =
          "Every price update and stock count changes automatically via Shopify Storefront queries.",
      ),
      ShopifyIntegrationCapability(
        title = "Orders Appear in Shopify",
        detail =
          "Mobile checkouts route straight into your existing Shopify Orders dashboard for unified fulfillment.",
      ),
      ShopifyIntegrationCapability(
        title = "Existing Payment Settings Respected",
        detail =
          "UPI, cards, wallets, and Cash on Delivery rules configured in Shopify apply automatically at checkout.",
      ),
      ShopifyIntegrationCapability(
        title = "Zero-Rebuild Launch Activation",
        detail =
          "Shopping modules unlock remotely as soon as mynovacart.in publishes its live storefront flag.",
      ),
    )
}
