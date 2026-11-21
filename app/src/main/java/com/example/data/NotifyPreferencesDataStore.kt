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
import com.example.model.NotifyMeRegistration
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
  }

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

  val lastCheckedEpochMsFlow: Flow<Long?> =
    context.novaCartDataStore.data.map { prefs -> prefs[Keys.LAST_CHECKED_AT] }

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
}
