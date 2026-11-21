package com.example.data

import android.content.Context
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.novaCartDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "novacart_preferences")

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
}
