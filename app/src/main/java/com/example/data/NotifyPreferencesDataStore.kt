package com.example.data

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.CartItem
import com.example.model.NotifyMeRegistration
import com.example.model.NovaCartCatalogBlueprint
import com.example.model.OrderRecord
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.MessageDigest
import java.security.SecureRandom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.novaCartDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "novacart_preferences")

data class OwnerAdminConnectionConfig(
  val myShopifyDomain: String = "",
  val adminAccessToken: String = "",
) {
  val isConfigured: Boolean
    get() = myShopifyDomain.isNotBlank() && adminAccessToken.isNotBlank()
}

class NotifyPreferencesDataStore(private val context: Context) {

  private object Keys {
    val EMAIL = stringPreferencesKey("notify_email")
    val FULL_NAME = stringPreferencesKey("notify_full_name")
    val CATEGORY_IDS = stringSetPreferencesKey("notify_category_ids")
    val IS_SUBSCRIBED = booleanPreferencesKey("notify_is_subscribed")
    val REGISTERED_AT = longPreferencesKey("notify_registered_at")
    val LAST_CHECKED_AT = longPreferencesKey("store_last_checked_at")
    val STORE_URL = stringPreferencesKey("shopify_store_url")
    val WISHLIST_IDS = stringSetPreferencesKey("wishlist_product_ids")
    val CART_ITEMS_JSON = stringPreferencesKey("shopping_cart_json")
    val ORDER_HISTORY_JSON = stringPreferencesKey("order_history_json")
    val OWNER_PIN_HASH = stringPreferencesKey("owner_pin_hash")
    val OWNER_PIN_SALT = stringPreferencesKey("owner_pin_salt")
    val OWNER_MYSHOPIFY_DOMAIN = stringPreferencesKey("owner_myshopify_domain")
    val OWNER_ADMIN_TOKEN = stringPreferencesKey("owner_admin_token")
  }

  private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val cartListType = Types.newParameterizedType(List::class.java, CartItem::class.java)
  private val cartAdapter = moshi.adapter<List<CartItem>>(cartListType)
  private val orderListType = Types.newParameterizedType(List::class.java, OrderRecord::class.java)
  private val orderAdapter = moshi.adapter<List<OrderRecord>>(orderListType)

  val registrationFlow: Flow<NotifyMeRegistration> =
    context.novaCartDataStore.data.map { prefs ->
      NotifyMeRegistration(
        email = prefs[Keys.EMAIL].orEmpty(),
        fullName = prefs[Keys.FULL_NAME].orEmpty(),
        interestedCategoryIds = prefs[Keys.CATEGORY_IDS].orEmpty(),
        isSubscribed = prefs[Keys.IS_SUBSCRIBED] ?: false,
        registeredAtEpochMs = prefs[Keys.REGISTERED_AT],
      )
    }

  val storeUrlFlow: Flow<String> =
    context.novaCartDataStore.data.map { prefs ->
      prefs[Keys.STORE_URL]?.takeIf { it.isNotBlank() }
        ?: NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL
    }

  val wishlistIdsFlow: Flow<Set<String>> =
    context.novaCartDataStore.data.map { prefs -> prefs[Keys.WISHLIST_IDS].orEmpty() }

  val cartItemsFlow: Flow<List<CartItem>> =
    context.novaCartDataStore.data.map { prefs ->
      val raw = prefs[Keys.CART_ITEMS_JSON]
      if (raw.isNullOrBlank()) emptyList()
      else runCatching { cartAdapter.fromJson(raw) }.getOrNull().orEmpty()
    }

  val orderHistoryFlow: Flow<List<OrderRecord>> =
    context.novaCartDataStore.data.map { prefs ->
      val raw = prefs[Keys.ORDER_HISTORY_JSON]
      if (raw.isNullOrBlank()) emptyList()
      else runCatching { orderAdapter.fromJson(raw) }.getOrNull().orEmpty()
    }

  val hasOwnerPinFlow: Flow<Boolean> =
    context.novaCartDataStore.data.map { prefs ->
      !prefs[Keys.OWNER_PIN_HASH].isNullOrBlank() && !prefs[Keys.OWNER_PIN_SALT].isNullOrBlank()
    }

  val ownerAdminConfigFlow: Flow<OwnerAdminConnectionConfig> =
    context.novaCartDataStore.data.map { prefs ->
      OwnerAdminConnectionConfig(
        myShopifyDomain = prefs[Keys.OWNER_MYSHOPIFY_DOMAIN].orEmpty(),
        adminAccessToken = prefs[Keys.OWNER_ADMIN_TOKEN].orEmpty(),
      )
    }

  suspend fun setOwnerPin(pin: String) {
    val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val saltB64 = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    val hashB64 = hashPin(pin = pin, saltBytes = saltBytes)
    context.novaCartDataStore.edit { prefs ->
      prefs[Keys.OWNER_PIN_SALT] = saltB64
      prefs[Keys.OWNER_PIN_HASH] = hashB64
    }
  }

  suspend fun verifyOwnerPin(pin: String): Boolean {
    val prefs = context.novaCartDataStore.data.first()
    val saltB64 = prefs[Keys.OWNER_PIN_SALT] ?: return false
    val expectedHash = prefs[Keys.OWNER_PIN_HASH] ?: return false
    val saltBytes = runCatching { Base64.decode(saltB64, Base64.NO_WRAP) }.getOrNull() ?: return false
    val actualHash = hashPin(pin = pin, saltBytes = saltBytes)
    return MessageDigest.isEqual(actualHash.toByteArray(), expectedHash.toByteArray())
  }

  suspend fun saveOwnerAdminConfig(domain: String, token: String) {
    context.novaCartDataStore.edit { prefs ->
      prefs[Keys.OWNER_MYSHOPIFY_DOMAIN] = domain
      prefs[Keys.OWNER_ADMIN_TOKEN] = token
    }
  }

  suspend fun clearOwnerAdminConfig() {
    context.novaCartDataStore.edit { prefs ->
      prefs.remove(Keys.OWNER_MYSHOPIFY_DOMAIN)
      prefs.remove(Keys.OWNER_ADMIN_TOKEN)
    }
  }

  suspend fun saveNotifyRegistration(
    email: String,
    fullName: String,
    categoryIds: Set<String>,
  ) {
    val now = System.currentTimeMillis()
    context.novaCartDataStore.edit { prefs ->
      prefs[Keys.EMAIL] = email.trim()
      prefs[Keys.FULL_NAME] = fullName.trim()
      prefs[Keys.CATEGORY_IDS] = categoryIds
      prefs[Keys.IS_SUBSCRIBED] = true
      prefs[Keys.REGISTERED_AT] = now
    }
  }

  suspend fun toggleCategoryInterest(categoryId: String) {
    context.novaCartDataStore.edit { prefs ->
      val current = prefs[Keys.CATEGORY_IDS].orEmpty().toMutableSet()
      if (!current.add(categoryId)) {
        current.remove(categoryId)
      }
      prefs[Keys.CATEGORY_IDS] = current
    }
  }

  suspend fun clearNotifyRegistration() {
    context.novaCartDataStore.edit { prefs ->
      prefs[Keys.IS_SUBSCRIBED] = false
      prefs.remove(Keys.REGISTERED_AT)
    }
  }

  suspend fun recordStoreCheckTimestamp(epochMs: Long) {
    context.novaCartDataStore.edit { prefs -> prefs[Keys.LAST_CHECKED_AT] = epochMs }
  }

  suspend fun saveStoreUrl(url: String) {
    context.novaCartDataStore.edit { prefs -> prefs[Keys.STORE_URL] = url }
  }

  suspend fun toggleWishlist(productId: Long) {
    val key = productId.toString()
    context.novaCartDataStore.edit { prefs ->
      val current = prefs[Keys.WISHLIST_IDS].orEmpty().toMutableSet()
      if (!current.add(key)) {
        current.remove(key)
      }
      prefs[Keys.WISHLIST_IDS] = current
    }
  }

  suspend fun saveCartItems(items: List<CartItem>) {
    context.novaCartDataStore.edit { prefs ->
      prefs[Keys.CART_ITEMS_JSON] = cartAdapter.toJson(items)
    }
  }

  suspend fun appendOrderRecord(order: OrderRecord) {
    context.novaCartDataStore.edit { prefs ->
      val existing =
        prefs[Keys.ORDER_HISTORY_JSON]
          ?.takeIf { it.isNotBlank() }
          ?.let { runCatching { orderAdapter.fromJson(it) }.getOrNull() }
          .orEmpty()
      prefs[Keys.ORDER_HISTORY_JSON] = orderAdapter.toJson(listOf(order) + existing)
    }
  }

  private fun hashPin(pin: String, saltBytes: ByteArray): String {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(saltBytes)
    digest.update(pin.trim().toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(digest.digest(), Base64.NO_WRAP)
  }
}
