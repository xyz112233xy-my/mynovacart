package com.example.data

import android.net.Uri
import com.example.model.CartItem
import com.example.model.NotifyMeRegistration
import com.example.model.NovaCartCatalogBlueprint
import com.example.model.OrderRecord
import com.example.model.ShopProduct
import com.example.model.ShopVariant
import com.example.model.StoreLaunchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class ShopifyCatalogSyncResult(
  val status: StoreLaunchStatus,
  val products: List<ShopProduct>,
)

class NovaCartRepository(
  private val preferencesStore: NotifyPreferencesDataStore,
  private val storefrontService: ShopifyStorefrontService = ShopifyStorefrontService.create(),
) {

  val registrationFlow: Flow<NotifyMeRegistration> = preferencesStore.registrationFlow
  val storeUrlFlow: Flow<String> = preferencesStore.storeUrlFlow
  val wishlistIdsFlow: Flow<Set<String>> = preferencesStore.wishlistIdsFlow
  val cartItemsFlow: Flow<List<CartItem>> = preferencesStore.cartItemsFlow
  val orderHistoryFlow: Flow<List<OrderRecord>> = preferencesStore.orderHistoryFlow
  val hasOwnerPinFlow: Flow<Boolean> = preferencesStore.hasOwnerPinFlow
  val ownerAdminConfigFlow: Flow<OwnerAdminConnectionConfig> = preferencesStore.ownerAdminConfigFlow

  suspend fun saveNotifyRegistration(email: String, fullName: String, categoryIds: Set<String>) {
    preferencesStore.saveNotifyRegistration(email = email, fullName = fullName, categoryIds = categoryIds)
  }

  suspend fun toggleCategoryInterest(categoryId: String) {
    preferencesStore.toggleCategoryInterest(categoryId)
  }

  suspend fun unsubscribeLaunchAlert() {
    preferencesStore.clearNotifyRegistration()
  }

  suspend fun saveStoreUrl(rawUrl: String): String {
    val normalized = normalizeStoreUrl(rawUrl)
    preferencesStore.saveStoreUrl(normalized)
    return normalized
  }

  suspend fun toggleWishlist(productId: Long) {
    preferencesStore.toggleWishlist(productId)
  }

  suspend fun saveCartItems(items: List<CartItem>) {
    preferencesStore.saveCartItems(items)
  }

  suspend fun appendOrderRecord(order: OrderRecord) {
    preferencesStore.appendOrderRecord(order)
  }

  suspend fun setOwnerPin(pin: String) {
    preferencesStore.setOwnerPin(pin)
  }

  suspend fun verifyOwnerPin(pin: String): Boolean {
    return preferencesStore.verifyOwnerPin(pin)
  }

  suspend fun saveOwnerAdminConfig(rawDomain: String, rawToken: String): Result<OwnerAdminConnectionConfig> {
    val cleanDomain = normalizeShopifyAdminHost(rawDomain)
      ?: return Result.failure(IllegalArgumentException("Enter a valid Shopify domain (e.g. yourstore.myshopify.com)."))
    val cleanToken = rawToken.trim()
    if (cleanToken.isEmpty()) {
      return Result.failure(IllegalArgumentException("Enter your Shopify Admin API access token."))
    }
    preferencesStore.saveOwnerAdminConfig(cleanDomain, cleanToken)
    return Result.success(OwnerAdminConnectionConfig(cleanDomain, cleanToken))
  }

  suspend fun clearOwnerAdminConfig() {
    preferencesStore.clearOwnerAdminConfig()
  }

  suspend fun createShopifyProductAsOwner(
    config: OwnerAdminConnectionConfig,
    title: String,
    price: Double,
    inventoryQuantity: Int,
    categoryTitle: String,
    description: String,
  ): Result<ShopifyAdminCreatedProduct> =
    withContext(Dispatchers.IO) {
      val host = normalizeShopifyAdminHost(config.myShopifyDomain)
        ?: return@withContext Result.failure(IllegalArgumentException("Invalid Shopify Admin host."))
      val token = config.adminAccessToken.trim()
      if (token.isEmpty()) {
        return@withContext Result.failure(IllegalArgumentException("Admin access token is missing."))
      }
      val cleanTitle = title.trim()
      if (cleanTitle.isEmpty()) {
        return@withContext Result.failure(IllegalArgumentException("Product title is required."))
      }
      if (price <= 0.0) {
        return@withContext Result.failure(IllegalArgumentException("Enter a valid price greater than 0."))
      }

      val safeDescription =
        android.text.TextUtils.htmlEncode(description.trim()).replace("\n", "<br/>")
      val request =
        ShopifyAdminCreateProductRequest(
          product =
            ShopifyAdminProductInput(
              title = cleanTitle,
              bodyHtml = if (safeDescription.isNotBlank()) "<p>$safeDescription</p>" else "",
              vendor = "NOVA CART",
              productType = categoryTitle,
              tags = categoryTitle,
              status = "active",
              variants =
                listOf(
                  ShopifyAdminVariantInput(
                    price = String.format(java.util.Locale.US, "%.2f", price),
                    inventoryManagement = "shopify",
                    inventoryQuantity = inventoryQuantity.coerceAtLeast(0),
                  )
                ),
            )
        )

      val response =
        runCatching {
          storefrontService.createAdminProduct(
            url = "https://$host/admin/api/2024-10/products.json",
            adminAccessToken = token,
            request = request,
          )
        }.getOrElse { err ->
          return@withContext Result.failure(err)
        }

      if (response.isSuccessful) {
        val created = response.body()?.product
        if (created != null) {
          return@withContext Result.success(created)
        }
      }
      Result.failure(
        IllegalStateException("Shopify rejected product creation (HTTP ${response.code()}). Check Admin token scopes (write_products).")
      )
    }

  suspend fun fetchShopifyOrdersAsOwner(
    config: OwnerAdminConnectionConfig,
  ): Result<List<ShopifyAdminOrderDto>> =
    withContext(Dispatchers.IO) {
      val host = normalizeShopifyAdminHost(config.myShopifyDomain)
        ?: return@withContext Result.failure(IllegalArgumentException("Invalid Shopify Admin host."))
      val token = config.adminAccessToken.trim()
      if (token.isEmpty()) {
        return@withContext Result.failure(IllegalArgumentException("Admin access token is missing."))
      }
      val response =
        runCatching {
          storefrontService.fetchAdminOrders(
            url = "https://$host/admin/api/2024-10/orders.json?status=any&limit=20",
            adminAccessToken = token,
          )
        }.getOrElse { err ->
          return@withContext Result.failure(err)
        }

      if (response.isSuccessful) {
        return@withContext Result.success(response.body()?.orders.orEmpty())
      }
      Result.failure(
        IllegalStateException("Unable to load orders (HTTP ${response.code()}). Ensure read_orders scope is enabled.")
      )
    }

  suspend fun syncShopifyCatalog(rawStoreUrl: String): ShopifyCatalogSyncResult =
    withContext(Dispatchers.IO) {
      val now = System.currentTimeMillis()
      val baseUrl = normalizeStoreUrl(rawStoreUrl)
      preferencesStore.recordStoreCheckTimestamp(now)

      val collectionsResp = runCatching {
        storefrontService.fetchCollectionsJson("$baseUrl/collections.json?limit=50")
      }
      val productsResp = runCatching {
        storefrontService.fetchProductsJson("$baseUrl/products.json?limit=250")
      }

      val productResponse = productsResp.getOrNull()
      val collectionResponse = collectionsResp.getOrNull()

      if (productResponse != null && productResponse.isSuccessful) {
        val rawProducts = productResponse.body()?.products.orEmpty()
        val rawCollections = collectionResponse?.body()?.collections.orEmpty()

        // Map collection products if collections exist
        val handleToCategoryId = mutableMapOf<String, String>()
        rawCollections.forEach { collection ->
          val matchedCat = matchCategoryId(collection.handle, collection.title, emptyList())
          if (matchedCat != null) {
            runCatching {
              storefrontService.fetchProductsJson(
                "$baseUrl/collections/${collection.handle}/products.json?limit=250"
              )
            }.getOrNull()?.body()?.products?.forEach { cp ->
              handleToCategoryId[cp.handle] = matchedCat
            }
          }
        }

        val mappedProducts =
          rawProducts.map { raw ->
            val categoryId =
              handleToCategoryId[raw.handle]
                ?: matchCategoryId(
                  handle = raw.handle,
                  title = raw.productType ?: raw.title,
                  tags = raw.tags,
                )
            val variants =
              raw.variants.map { v ->
                ShopVariant(
                  id = v.id,
                  title = v.title.ifBlank { "Standard" },
                  priceAmount = v.price.toDoubleOrNull() ?: 0.0,
                  compareAtPriceAmount = v.compareAtPrice?.toDoubleOrNull(),
                  available = v.available,
                )
              }
            val images = raw.images.mapNotNull { it.src.takeIf(String::isNotBlank) }
            ShopProduct(
              id = raw.id,
              title = raw.title,
              handle = raw.handle,
              description = stripHtmlTags(raw.bodyHtml.orEmpty()),
              vendor = raw.vendor.orEmpty().ifBlank { "NOVA CART" },
              productType = raw.productType.orEmpty(),
              categoryId = categoryId,
              imageUrl = images.firstOrNull(),
              imageUrls = images,
              variants = variants,
              available = variants.any { it.available },
            )
          }

        val status =
          StoreLaunchStatus(
            storeUrl = baseUrl,
            isStoreLaunched = true,
            isChecking = false,
            isWebsiteReachable = true,
            isPasswordProtected = false,
            syncedProductCount = mappedProducts.size,
            syncedCollectionCount = rawCollections.size,
            statusHeadline =
              if (mappedProducts.isNotEmpty()) "Connected • ${mappedProducts.size} Live Products"
              else "Connected to $baseUrl",
            statusDetail =
              if (mappedProducts.isNotEmpty()) {
                "Synced ${mappedProducts.size} products across ${rawCollections.size} Shopify collections. Orders go directly to your Shopify store."
              } else {
                "Store is reachable, but 0 published products were found at /products.json. Publish products in Shopify Admin and tap Sync."
              },
            lastCheckedEpochMs = now,
            shopifyStorefrontConfigured = true,
          )
        return@withContext ShopifyCatalogSyncResult(status = status, products = mappedProducts)
      }

      // Check if store is reachable (e.g. password-protected Shopify store redirects to /password)
      val pingResult = runCatching { storefrontService.pingUrl(baseUrl) }.getOrNull()
      val isReachable = pingResult != null && pingResult.code() in 200..499
      val finalUrl = pingResult?.raw()?.request?.url?.toString().orEmpty()
      val isPasswordPage =
        finalUrl.contains("/password", ignoreCase = true) ||
          productResponse?.raw()?.request?.url?.toString()?.contains("/password", ignoreCase = true) == true ||
          productResponse?.code() == 401

      val status =
        StoreLaunchStatus(
          storeUrl = baseUrl,
          isStoreLaunched = true,
          isChecking = false,
          isWebsiteReachable = isReachable,
          isPasswordProtected = isPasswordPage,
          syncedProductCount = 0,
          syncedCollectionCount = 0,
          statusHeadline =
            when {
              isPasswordPage -> "Shopify Store Password Active"
              isReachable -> "Storefront Connected • Awaiting Catalog"
              else -> "Unable to Reach $baseUrl"
            },
          statusDetail =
            when {
              isPasswordPage ->
                "Your Shopify store at $baseUrl currently has a storefront password enabled. Disable the password in Shopify Online Store > Preferences to sync products into the app."
              isReachable ->
                "Connected to $baseUrl, but /products.json is not returning Shopify product JSON yet. Verify your Shopify store URL in Store Settings."
              else ->
                "Could not connect to $baseUrl. Check your internet connection or verify your Shopify domain."
            },
          errorMessage = productsResp.exceptionOrNull()?.localizedMessage,
          lastCheckedEpochMs = now,
          shopifyStorefrontConfigured = false,
        )
      ShopifyCatalogSyncResult(status = status, products = emptyList())
    }

  companion object {
    fun normalizeStoreUrl(raw: String): String {
      val trimmed = raw.trim().trimEnd('/')
      if (trimmed.isEmpty()) return NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL
      val withScheme =
        if (trimmed.startsWith("https://", ignoreCase = true)) {
          trimmed
        } else if (trimmed.startsWith("http://", ignoreCase = true)) {
          "https://" + trimmed.removePrefix("http://").removePrefix("HTTP://")
        } else {
          "https://$trimmed"
        }
      val host = Uri.parse(withScheme).host?.lowercase().orEmpty()
      if (host.isBlank() || isPrivateOrLoopbackHost(host)) {
        return NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL
      }
      return "https://$host"
    }

    fun normalizeShopifyAdminHost(raw: String): String? {
      val cleaned =
        raw.trim()
          .removePrefix("https://")
          .removePrefix("http://")
          .substringBefore("/")
          .substringBefore("?")
          .lowercase()
      if (cleaned.isBlank() || !cleaned.contains(".") || isPrivateOrLoopbackHost(cleaned)) {
        return null
      }
      if (!Regex("^[a-z0-9.-]+$").matches(cleaned)) return null
      return cleaned
    }

    private fun isPrivateOrLoopbackHost(host: String): Boolean {
      return host == "localhost" ||
        host.startsWith("127.") ||
        host.startsWith("10.") ||
        host.startsWith("192.168.") ||
        host == "169.254.169.254" ||
        host.startsWith("172.16.") ||
        host == "::1"
    }

    fun buildShopifyCheckoutUrl(
      storeUrl: String,
      items: List<CartItem>,
      customerEmail: String? = null,
    ): String {
      val base = normalizeStoreUrl(storeUrl)
      val lineItems = items.joinToString(",") { "${it.variantId}:${it.quantity}" }
      val builder = Uri.parse("$base/cart/$lineItems").buildUpon()
      builder.appendQueryParameter("note", "Order via NOVA CART Android App")
      if (!customerEmail.isNullOrBlank()) {
        builder.appendQueryParameter("checkout[email]", customerEmail.trim())
      }
      return builder.build().toString()
    }

    fun stripHtmlTags(html: String): String {
      if (html.isBlank()) return ""
      return html
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
        .replace(Regex("<[^>]+>"), "")
        .replace("&amp;", "&")
        .replace("&nbsp;", " ")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .trim()
    }

    fun matchCategoryId(handle: String, title: String, tags: List<String>): String? {
      val haystack =
        (listOf(handle, title) + tags).joinToString(" ").lowercase()
      return when {
        haystack.contains("home") || haystack.contains("decor") -> "home_decor"
        haystack.contains("kitchen") || haystack.contains("cook") -> "kitchen_accessories"
        haystack.contains("beauty") || haystack.contains("skin") || haystack.contains("care") ->
          "beauty_care"
        haystack.contains("gadget") || haystack.contains("tech") || haystack.contains("electronic") ->
          "daily_life_gadgets"
        haystack.contains("kid") || haystack.contains("toy") || haystack.contains("baby") -> "kids"
        else -> null
      }
    }
  }
}
