package com.example.data

import com.squareup.moshi.Json

/**
 * Shopify public storefront catalog DTOs (/products.json & /collections.json)
 * plus Storefront GraphQL DTOs and optional remote config.
 */
data class ShopifyProductsJsonResponse(
  @Json(name = "products") val products: List<ShopifyRestProduct> = emptyList(),
)

data class ShopifyRestProduct(
  @Json(name = "id") val id: Long,
  @Json(name = "title") val title: String = "",
  @Json(name = "handle") val handle: String = "",
  @Json(name = "body_html") val bodyHtml: String? = null,
  @Json(name = "vendor") val vendor: String? = null,
  @Json(name = "product_type") val productType: String? = null,
  @Json(name = "tags") val tags: List<String> = emptyList(),
  @Json(name = "variants") val variants: List<ShopifyRestVariant> = emptyList(),
  @Json(name = "images") val images: List<ShopifyRestImage> = emptyList(),
)

data class ShopifyRestVariant(
  @Json(name = "id") val id: Long,
  @Json(name = "title") val title: String = "Default Title",
  @Json(name = "price") val price: String = "0.00",
  @Json(name = "compare_at_price") val compareAtPrice: String? = null,
  @Json(name = "available") val available: Boolean = true,
  @Json(name = "sku") val sku: String? = null,
)

data class ShopifyRestImage(
  @Json(name = "id") val id: Long? = null,
  @Json(name = "src") val src: String = "",
  @Json(name = "alt") val alt: String? = null,
)

data class ShopifyCollectionsJsonResponse(
  @Json(name = "collections") val collections: List<ShopifyRestCollection> = emptyList(),
)

data class ShopifyRestCollection(
  @Json(name = "id") val id: Long,
  @Json(name = "handle") val handle: String = "",
  @Json(name = "title") val title: String = "",
  @Json(name = "description") val description: String? = null,
  @Json(name = "image") val image: ShopifyCollectionImage? = null,
)

data class ShopifyCollectionImage(
  @Json(name = "src") val src: String? = null,
)

data class RemoteAppConfig(
  @Json(name = "store_launched") val storeLaunched: Boolean = true,
  @Json(name = "store_url") val storeUrl: String = "https://mynovacart.in",
  @Json(name = "storefront_api_version") val storefrontApiVersion: String = "2024-10",
  @Json(name = "storefront_access_token") val storefrontAccessToken: String? = null,
  @Json(name = "announcement_message") val announcementMessage: String? = null,
)

data class ShopifyGraphQlRequest(
  @Json(name = "query") val query: String,
  @Json(name = "variables") val variables: Map<String, String> = emptyMap(),
)

data class ShopifyGraphQlResponse(
  @Json(name = "data") val data: ShopifyStorefrontData? = null,
)

data class ShopifyStorefrontData(
  @Json(name = "shop") val shop: ShopifyShopInfo? = null,
  @Json(name = "collections") val collections: ShopifyCollectionConnection? = null,
  @Json(name = "products") val products: ShopifyProductConnection? = null,
)

data class ShopifyShopInfo(
  @Json(name = "name") val name: String,
  @Json(name = "primaryDomain") val primaryDomain: ShopifyDomain? = null,
)

data class ShopifyDomain(
  @Json(name = "url") val url: String,
)

data class ShopifyCollectionConnection(
  @Json(name = "nodes") val nodes: List<ShopifyCollectionNode> = emptyList(),
)

data class ShopifyCollectionNode(
  @Json(name = "id") val id: String,
  @Json(name = "handle") val handle: String,
  @Json(name = "title") val title: String,
)

data class ShopifyProductConnection(
  @Json(name = "nodes") val nodes: List<ShopifyProductNode> = emptyList(),
)

data class ShopifyProductNode(
  @Json(name = "id") val id: String,
  @Json(name = "handle") val handle: String,
  @Json(name = "title") val title: String,
  @Json(name = "availableForSale") val availableForSale: Boolean = false,
  @Json(name = "priceRange") val priceRange: ShopifyPriceRange? = null,
)

data class ShopifyPriceRange(
  @Json(name = "minVariantPrice") val minVariantPrice: ShopifyMoney? = null,
)

data class ShopifyMoney(
  @Json(name = "amount") val amount: String,
  @Json(name = "currencyCode") val currencyCode: String,
)

// Owner Studio: Shopify Admin REST API DTOs (authenticated solely via owner's device-local token)
data class ShopifyAdminCreateProductRequest(
  @Json(name = "product") val product: ShopifyAdminProductInput,
)

data class ShopifyAdminProductInput(
  @Json(name = "title") val title: String,
  @Json(name = "body_html") val bodyHtml: String,
  @Json(name = "vendor") val vendor: String = "NOVA CART",
  @Json(name = "product_type") val productType: String = "",
  @Json(name = "tags") val tags: String = "",
  @Json(name = "status") val status: String = "active",
  @Json(name = "variants") val variants: List<ShopifyAdminVariantInput>,
)

data class ShopifyAdminVariantInput(
  @Json(name = "price") val price: String,
  @Json(name = "inventory_management") val inventoryManagement: String = "shopify",
  @Json(name = "inventory_quantity") val inventoryQuantity: Int = 10,
)

data class ShopifyAdminProductResponse(
  @Json(name = "product") val product: ShopifyAdminCreatedProduct? = null,
)

data class ShopifyAdminCreatedProduct(
  @Json(name = "id") val id: Long,
  @Json(name = "title") val title: String = "",
)

data class ShopifyAdminOrdersResponse(
  @Json(name = "orders") val orders: List<ShopifyAdminOrderDto> = emptyList(),
)

data class ShopifyAdminOrderDto(
  @Json(name = "id") val id: Long,
  @Json(name = "name") val name: String = "",
  @Json(name = "total_price") val totalPrice: String = "0.00",
  @Json(name = "financial_status") val financialStatus: String? = null,
  @Json(name = "fulfillment_status") val fulfillmentStatus: String? = null,
  @Json(name = "created_at") val createdAt: String = "",
  @Json(name = "email") val email: String? = null,
)
