package com.example.data

import com.example.model.NotifyMeRegistration
import com.example.model.StoreLaunchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NovaCartRepository(
  private val preferencesStore: NotifyPreferencesDataStore,
  private val storefrontService: ShopifyStorefrontService = ShopifyStorefrontService.create(),
) {

  val registrationFlow: Flow<NotifyMeRegistration> = preferencesStore.registrationFlow

  val lastCheckedFlow: Flow<Long?> = preferencesStore.lastCheckedEpochMsFlow

  suspend fun saveNotifyRegistration(
    email: String,
    fullName: String,
    categoryIds: Set<String>,
  ) {
    preferencesStore.saveNotifyRegistration(
      email = email,
      fullName = fullName,
      categoryIds = categoryIds,
    )
  }

  suspend fun toggleCategoryInterest(categoryId: String) {
    preferencesStore.toggleCategoryInterest(categoryId)
  }

  suspend fun unsubscribeLaunchAlert() {
    preferencesStore.clearNotifyRegistration()
  }

  /**
   * Checks https://mynovacart.in reachability and remote launch config.
   * While the store is Coming Soon (store_launched = false or config absent),
   * shopping features remain strictly disabled so no fake catalog is ever shown.
   */
  suspend fun checkStoreLaunchStatus(): StoreLaunchStatus =
    withContext(Dispatchers.IO) {
      val now = System.currentTimeMillis()
      preferencesStore.recordStoreCheckTimestamp(now)

      val remoteConfig =
        runCatching { storefrontService.fetchRemoteAppConfig() }.getOrNull()?.let { response ->
          if (response.isSuccessful) response.body() else null
        }

      if (remoteConfig != null && remoteConfig.storeLaunched) {
        val token = remoteConfig.storefrontAccessToken?.takeIf { it.isNotBlank() }
        val shopVerified =
          if (token != null) {
            runCatching {
                storefrontService.queryStorefrontGraphQl(
                  apiVersion = remoteConfig.storefrontApiVersion,
                  storefrontAccessToken = token,
                  request = ShopifyGraphQlRequest(ShopifyGraphQlQueries.SHOP_AND_COLLECTIONS),
                )
              }
              .getOrNull()
              ?.isSuccessful == true
          } else {
            false
          }

        return@withContext StoreLaunchStatus(
          storeUrl = remoteConfig.storeUrl,
          isStoreLaunched = shopVerified,
          isChecking = false,
          isWebsiteReachable = true,
          statusHeadline =
            if (shopVerified) "Store Connected — Live on Shopify"
            else "Coming Soon — Storefront Pending Activation",
          statusDetail =
            if (shopVerified) {
              "Connected to ${remoteConfig.storeUrl} via Shopify Storefront API."
            } else {
              "Storefront launch flag detected; awaiting live Shopify Storefront token."
            },
          lastCheckedEpochMs = now,
          shopifyStorefrontConfigured = shopVerified,
        )
      }

      val reachable =
        runCatching {
            val ping = storefrontService.pingStorefront()
            ping.code() in 200..499
          }
          .getOrDefault(false)

      StoreLaunchStatus(
        storeUrl = "https://mynovacart.in",
        isStoreLaunched = false,
        isChecking = false,
        isWebsiteReachable = reachable,
        statusHeadline = "Coming Soon — Pre-Launch Mode",
        statusDetail =
          if (reachable) {
            "mynovacart.in is online in Coming Soon mode. Shopping modules unlock automatically on launch day."
          } else {
            "mynovacart.in is preparing for launch. Shopping features remain disabled until the store officially opens."
          },
        lastCheckedEpochMs = now,
        shopifyStorefrontConfigured = false,
      )
    }
}
