package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Remote launch configuration schema that https://mynovacart.in can serve at
 * /pages/app-config.json (or via Storefront check) to activate full shopping
 * features without rebuilding the Android APK.
 */
@JsonClass(generateAdapter = true)
data class RemoteAppConfig(
  @Json(name = "store_launched") val storeLaunched: Boolean = false,
  @Json(name = "store_url") val storeUrl: String = "https://mynovacart.in",
  @Json(name = "storefront_api_version") val storefrontApiVersion: String = "2024-10",
  @Json(name = "storefront_access_token") val storefrontAccessToken: String? = null,
  @Json(name = "announcement_message") val announcementMessage: String? = null,
  @Json(name = "instagram_url") val instagramUrl: String? = null,
  @Json(name = "facebook_url") val facebookUrl: String? = null,
  @Json(name = "youtube_url") val youtubeUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyGraphQlRequest(
  @Json(name = "query") val query: String,
  @Json(name = "variables") val variables: Map<String, String> = emptyMap(),
)

@JsonClass(generateAdapter = true)
data class ShopifyGraphQlResponse(
  @Json(name = "data") val data: ShopifyStorefrontData? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyStorefrontData(
  @Json(name = "shop") val shop: ShopifyShopInfo? = null,
  @Json(name = "collections") val collections: ShopifyCollectionConnection? = null,
  @Json(name = "products") val products: ShopifyProductConnection? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyShopInfo(
  @Json(name = "name") val name: String,
  @Json(name = "primaryDomain") val primaryDomain: ShopifyDomain? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyDomain(
  @Json(name = "url") val url: String,
)

@JsonClass(generateAdapter = true)
data class ShopifyCollectionConnection(
  @Json(name = "nodes") val nodes: List<ShopifyCollectionNode> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class ShopifyCollectionNode(
  @Json(name = "id") val id: String,
  @Json(name = "handle") val handle: String,
  @Json(name = "title") val title: String,
)

@JsonClass(generateAdapter = true)
data class ShopifyProductConnection(
  @Json(name = "nodes") val nodes: List<ShopifyProductNode> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class ShopifyProductNode(
  @Json(name = "id") val id: String,
  @Json(name = "handle") val handle: String,
  @Json(name = "title") val title: String,
  @Json(name = "availableForSale") val availableForSale: Boolean = false,
  @Json(name = "priceRange") val priceRange: ShopifyPriceRange? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyPriceRange(
  @Json(name = "minVariantPrice") val minVariantPrice: ShopifyMoney? = null,
)

@JsonClass(generateAdapter = true)
data class ShopifyMoney(
  @Json(name = "amount") val amount: String,
  @Json(name = "currencyCode") val currencyCode: String,
)
