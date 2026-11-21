package com.example.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NotifyPreferencesDataStore
import com.example.data.NovaCartRepository
import com.example.data.OwnerAdminConnectionConfig
import com.example.data.ShopifyAdminOrderDto
import com.example.model.CartItem
import com.example.model.FutureFeature
import com.example.model.LaunchCategory
import com.example.model.NotifyMeRegistration
import com.example.model.NovaCartCatalogBlueprint
import com.example.model.OrderRecord
import com.example.model.ProductSortOption
import com.example.model.ShopProduct
import com.example.model.ShopVariant
import com.example.model.StoreLaunchStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WebPortalRequest(
  val title: String,
  val url: String,
  val isCheckout: Boolean = false,
  val checkoutTotalAmount: Double = 0.0,
  val checkoutItemCount: Int = 0,
  val checkoutSummary: String = "",
)

data class NovaCartUiState(
  val showLaunchSplash: Boolean = true,
  val categories: List<LaunchCategory> = NovaCartCatalogBlueprint.launchCategories,
  val futureFeatures: List<FutureFeature> = NovaCartCatalogBlueprint.futureShoppingFeatures,
  val registration: NotifyMeRegistration = NotifyMeRegistration(),
  val storeStatus: StoreLaunchStatus = StoreLaunchStatus(),
  val storeUrlInput: String = NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL,
  val products: List<ShopProduct> = emptyList(),
  val searchQuery: String = "",
  val selectedCategoryId: String? = null,
  val wishlistOnly: Boolean = false,
  val sortOption: ProductSortOption = ProductSortOption.FEATURED,
  val wishlistIds: Set<String> = emptySet(),
  val cartItems: List<CartItem> = emptyList(),
  val orderHistory: List<OrderRecord> = emptyList(),
  val selectedProductForDetail: ShopProduct? = null,
  val activeWebPortal: WebPortalRequest? = null,
  val statusMessageBanner: String? = null,
  // Owner Studio (protected behind Owner PIN, never shown in normal customer flows)
  val isOwnerPinSet: Boolean = false,
  val isOwnerGateVisible: Boolean = false,
  val isOwnerStudioVisible: Boolean = false,
  val ownerPinError: String? = null,
  val ownerAdminConfig: OwnerAdminConnectionConfig = OwnerAdminConnectionConfig(),
  val ownerAdminDomainInput: String = "",
  val ownerAdminTokenInput: String = "",
  val isOwnerActionBusy: Boolean = false,
  val ownerActionFeedback: String? = null,
  val ownerAdminOrders: List<ShopifyAdminOrderDto> = emptyList(),
  // Legacy optional registration fields
  val isNotifySheetVisible: Boolean = false,
  val notifyEmailInput: String = "",
  val notifyNameInput: String = "",
  val notifySelectedCategoryIds: Set<String> = emptySet(),
  val notifyEmailError: String? = null,
  val notifyJustSaved: Boolean = false,
) {
  val cartItemCount: Int
    get() = cartItems.sumOf { it.quantity }

  val cartSubtotal: Double
    get() = cartItems.sumOf { it.priceAmount * it.quantity }

  val filteredProducts: List<ShopProduct>
    get() {
      val query = searchQuery.trim().lowercase()
      val filtered =
        products.filter { product ->
          val matchesQuery =
            query.isEmpty() ||
              product.title.lowercase().contains(query) ||
              product.description.lowercase().contains(query) ||
              product.vendor.lowercase().contains(query) ||
              product.productType.lowercase().contains(query)
          val matchesCategory =
            selectedCategoryId == null || product.categoryId == selectedCategoryId
          val matchesWishlist =
            !wishlistOnly || wishlistIds.contains(product.id.toString())
          matchesQuery && matchesCategory && matchesWishlist
        }

      return when (sortOption) {
        ProductSortOption.FEATURED -> filtered
        ProductSortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.displayPrice }
        ProductSortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.displayPrice }
        ProductSortOption.NAME_A_TO_Z -> filtered.sortedBy { it.title.lowercase() }
      }
    }
}

class NovaCartViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = NovaCartRepository(NotifyPreferencesDataStore(application))

  private val _uiState = MutableStateFlow(NovaCartUiState())
  val uiState: StateFlow<NovaCartUiState> = _uiState.asStateFlow()

  init {
    observePersistedState()
    runLaunchSplashSequence()
    viewModelScope.launch {
      val savedUrl = repository.storeUrlFlow.first()
      _uiState.update { it.copy(storeUrlInput = savedUrl) }
      syncShopifyCatalog(savedUrl)
    }
  }

  private fun observePersistedState() {
    viewModelScope.launch {
      repository.registrationFlow.collect { saved ->
        _uiState.update { current ->
          current.copy(
            registration = saved,
            notifyEmailInput =
              if (current.isNotifySheetVisible) current.notifyEmailInput else saved.email,
            notifyNameInput =
              if (current.isNotifySheetVisible) current.notifyNameInput else saved.fullName,
            notifySelectedCategoryIds =
              if (current.isNotifySheetVisible) current.notifySelectedCategoryIds
              else if (saved.interestedCategoryIds.isNotEmpty()) saved.interestedCategoryIds
              else current.categories.map { it.id }.toSet(),
          )
        }
      }
    }

    viewModelScope.launch {
      repository.wishlistIdsFlow.collect { ids ->
        _uiState.update { it.copy(wishlistIds = ids) }
      }
    }

    viewModelScope.launch {
      repository.cartItemsFlow.collect { items ->
        _uiState.update { it.copy(cartItems = items) }
      }
    }

    viewModelScope.launch {
      repository.orderHistoryFlow.collect { orders ->
        _uiState.update { it.copy(orderHistory = orders) }
      }
    }

    viewModelScope.launch {
      repository.hasOwnerPinFlow.collect { hasPin ->
        _uiState.update { it.copy(isOwnerPinSet = hasPin) }
      }
    }

    viewModelScope.launch {
      repository.ownerAdminConfigFlow.collect { config ->
        _uiState.update {
          it.copy(
            ownerAdminConfig = config,
            ownerAdminDomainInput = config.myShopifyDomain,
            ownerAdminTokenInput = config.adminAccessToken,
          )
        }
      }
    }
  }

  private fun runLaunchSplashSequence() {
    viewModelScope.launch {
      delay(1100L)
      _uiState.update { it.copy(showLaunchSplash = false) }
    }
  }

  fun dismissLaunchSplash() {
    _uiState.update { it.copy(showLaunchSplash = false) }
  }

  fun onStoreUrlInputChanged(value: String) {
    _uiState.update { it.copy(storeUrlInput = value) }
  }

  fun saveAndSyncStoreUrl() {
    viewModelScope.launch {
      val normalized = repository.saveStoreUrl(_uiState.value.storeUrlInput)
      _uiState.update { it.copy(storeUrlInput = normalized) }
      syncShopifyCatalog(normalized)
    }
  }

  fun refreshStoreCatalog() {
    syncShopifyCatalog(_uiState.value.storeStatus.storeUrl)
  }

  private fun syncShopifyCatalog(storeUrl: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(storeStatus = it.storeStatus.copy(isChecking = true)) }
      val result = repository.syncShopifyCatalog(storeUrl)
      _uiState.update {
        it.copy(
          storeStatus = result.status,
          products = result.products,
        )
      }
    }
  }

  fun onSearchQueryChanged(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun selectCategoryFilter(categoryId: String?) {
    _uiState.update { it.copy(selectedCategoryId = categoryId, wishlistOnly = false) }
  }

  fun toggleWishlistFilter() {
    _uiState.update { it.copy(wishlistOnly = !it.wishlistOnly) }
  }

  fun selectSortOption(option: ProductSortOption) {
    _uiState.update { it.copy(sortOption = option) }
  }

  fun openProductDetail(product: ShopProduct) {
    _uiState.update { it.copy(selectedProductForDetail = product) }
  }

  fun closeProductDetail() {
    _uiState.update { it.copy(selectedProductForDetail = null) }
  }

  fun toggleWishlist(productId: Long) {
    viewModelScope.launch { repository.toggleWishlist(productId) }
  }

  fun addToCart(product: ShopProduct, variant: ShopVariant? = product.primaryVariant, quantity: Int = 1) {
    val chosenVariant = variant ?: return
    val current = _uiState.value.cartItems.toMutableList()
    val existingIndex = current.indexOfFirst { it.variantId == chosenVariant.id }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = (existing.quantity + quantity).coerceAtMost(99))
    } else {
      current.add(
        CartItem(
          productId = product.id,
          variantId = chosenVariant.id,
          title = product.title,
          variantTitle = chosenVariant.title,
          priceAmount = chosenVariant.priceAmount,
          imageUrl = product.imageUrl,
          quantity = quantity.coerceAtLeast(1),
        )
      )
    }
    viewModelScope.launch {
      repository.saveCartItems(current)
      _uiState.update { it.copy(statusMessageBanner = "Added ${product.title} to cart") }
    }
  }

  fun updateCartQuantity(variantId: Long, delta: Int) {
    val current = _uiState.value.cartItems.toMutableList()
    val index = current.indexOfFirst { it.variantId == variantId }
    if (index < 0) return
    val updatedQty = current[index].quantity + delta
    if (updatedQty <= 0) {
      current.removeAt(index)
    } else {
      current[index] = current[index].copy(quantity = updatedQty.coerceAtMost(99))
    }
    viewModelScope.launch { repository.saveCartItems(current) }
  }

  fun removeFromCart(variantId: Long) {
    val current = _uiState.value.cartItems.filterNot { it.variantId == variantId }
    viewModelScope.launch { repository.saveCartItems(current) }
  }

  fun startShopifyCheckout() {
    val state = _uiState.value
    if (state.cartItems.isEmpty()) return
    val checkoutUrl =
      NovaCartRepository.buildShopifyCheckoutUrl(
        storeUrl = state.storeStatus.storeUrl,
        items = state.cartItems,
        customerEmail = state.registration.email.takeIf { it.isNotBlank() },
      )
    val summary =
      state.cartItems.joinToString(", ") { "${it.title} x${it.quantity}" }
    _uiState.update {
      it.copy(
        activeWebPortal =
          WebPortalRequest(
            title = "Secure Shopify Checkout",
            url = checkoutUrl,
            isCheckout = true,
            checkoutTotalAmount = state.cartSubtotal,
            checkoutItemCount = state.cartItemCount,
            checkoutSummary = summary,
          )
      )
    }
  }

  fun buyNowSingleProduct(product: ShopProduct, variant: ShopVariant, quantity: Int) {
    val singleItem =
      listOf(
        CartItem(
          productId = product.id,
          variantId = variant.id,
          title = product.title,
          variantTitle = variant.title,
          priceAmount = variant.priceAmount,
          imageUrl = product.imageUrl,
          quantity = quantity,
        )
      )
    val checkoutUrl =
      NovaCartRepository.buildShopifyCheckoutUrl(
        storeUrl = _uiState.value.storeStatus.storeUrl,
        items = singleItem,
        customerEmail = _uiState.value.registration.email.takeIf { it.isNotBlank() },
      )
    _uiState.update {
      it.copy(
        selectedProductForDetail = null,
        activeWebPortal =
          WebPortalRequest(
            title = "Express Shopify Checkout",
            url = checkoutUrl,
            isCheckout = true,
            checkoutTotalAmount = variant.priceAmount * quantity,
            checkoutItemCount = quantity,
            checkoutSummary = "${product.title} x$quantity",
          ),
      )
    }
  }

  fun onShopifyOrderCompleted(thankYouUrl: String) {
    val portal = _uiState.value.activeWebPortal ?: return
    if (!portal.isCheckout) return
    val order =
      OrderRecord(
        id = "NC-${System.currentTimeMillis().toString().takeLast(6)}",
        checkoutUrl = thankYouUrl,
        itemCount = portal.checkoutItemCount,
        totalAmount = portal.checkoutTotalAmount,
        summary = portal.checkoutSummary,
        createdAtEpochMs = System.currentTimeMillis(),
        status = "Placed on Shopify",
      )
    viewModelScope.launch {
      repository.appendOrderRecord(order)
      repository.saveCartItems(emptyList())
      _uiState.update {
        it.copy(
          statusMessageBanner = "Order ${order.id} placed via Shopify!",
        )
      }
    }
  }

  fun openStorefrontWebsite() {
    val base = _uiState.value.storeStatus.storeUrl
    _uiState.update {
      it.copy(
        activeWebPortal =
          WebPortalRequest(title = "NOVA CART Storefront", url = base, isCheckout = false)
      )
    }
  }

  fun openCustomerAccountPortal() {
    val base = _uiState.value.storeStatus.storeUrl.trimEnd('/')
    _uiState.update {
      it.copy(
        activeWebPortal =
          WebPortalRequest(
            title = "Customer Account",
            url = "$base/account",
            isCheckout = false,
          )
      )
    }
  }

  fun openOrdersPortal() {
    val base = _uiState.value.storeStatus.storeUrl.trimEnd('/')
    _uiState.update {
      it.copy(
        activeWebPortal =
          WebPortalRequest(
            title = "Order History & Tracking",
            url = "$base/account/orders",
            isCheckout = false,
          )
      )
    }
  }

  fun reopenOrderUrl(url: String, title: String) {
    _uiState.update {
      it.copy(activeWebPortal = WebPortalRequest(title = title, url = url, isCheckout = false))
    }
  }

  fun closeWebPortal() {
    _uiState.update { it.copy(activeWebPortal = null) }
  }

  fun consumeStatusBanner() {
    _uiState.update { it.copy(statusMessageBanner = null) }
  }

  // ---- Owner Studio (hidden store management mode) ----
  fun openOwnerAccessGate() {
    _uiState.update {
      it.copy(isOwnerGateVisible = true, ownerPinError = null)
    }
  }

  fun dismissOwnerAccessGate() {
    _uiState.update { it.copy(isOwnerGateVisible = false, ownerPinError = null) }
  }

  fun submitOwnerPin(rawPin: String) {
    val pin = rawPin.trim()
    if (pin.length < 4) {
      _uiState.update { it.copy(ownerPinError = "Enter at least 4 digits for Owner PIN.") }
      return
    }
    viewModelScope.launch {
      if (!_uiState.value.isOwnerPinSet) {
        repository.setOwnerPin(pin)
        _uiState.update {
          it.copy(
            isOwnerGateVisible = false,
            isOwnerStudioVisible = true,
            ownerPinError = null,
            ownerActionFeedback = "Owner PIN created on this device.",
          )
        }
      } else if (repository.verifyOwnerPin(pin)) {
        _uiState.update {
          it.copy(
            isOwnerGateVisible = false,
            isOwnerStudioVisible = true,
            ownerPinError = null,
          )
        }
        if (_uiState.value.ownerAdminConfig.isConfigured) {
          refreshOwnerOrders()
        }
      } else {
        _uiState.update { it.copy(ownerPinError = "Incorrect Owner PIN.") }
      }
    }
  }

  fun closeOwnerStudio() {
    _uiState.update { it.copy(isOwnerStudioVisible = false, ownerActionFeedback = null) }
  }

  fun onOwnerAdminDomainChanged(value: String) {
    _uiState.update { it.copy(ownerAdminDomainInput = value) }
  }

  fun onOwnerAdminTokenChanged(value: String) {
    _uiState.update { it.copy(ownerAdminTokenInput = value) }
  }

  fun saveOwnerAdminConnection() {
    val state = _uiState.value
    viewModelScope.launch {
      _uiState.update { it.copy(isOwnerActionBusy = true, ownerActionFeedback = null) }
      val result =
        repository.saveOwnerAdminConfig(
          rawDomain = state.ownerAdminDomainInput,
          rawToken = state.ownerAdminTokenInput,
        )
      result.fold(
        onSuccess = { savedConfig ->
          _uiState.update {
            it.copy(
              isOwnerActionBusy = false,
              ownerActionFeedback = "Connected Admin API to ${savedConfig.myShopifyDomain}",
            )
          }
          refreshOwnerOrders()
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isOwnerActionBusy = false,
              ownerActionFeedback = err.localizedMessage ?: "Failed to save connection.",
            )
          }
        },
      )
    }
  }

  fun disconnectOwnerAdmin() {
    viewModelScope.launch {
      repository.clearOwnerAdminConfig()
      _uiState.update {
        it.copy(
          ownerAdminDomainInput = "",
          ownerAdminTokenInput = "",
          ownerAdminOrders = emptyList(),
          ownerActionFeedback = "Admin API disconnected from this device.",
        )
      }
    }
  }

  fun createProductFromOwnerStudio(
    title: String,
    priceInput: String,
    stockInput: String,
    categoryTitle: String,
    description: String,
    onSuccessReset: () -> Unit,
  ) {
    val config = _uiState.value.ownerAdminConfig
    val price = priceInput.trim().toDoubleOrNull() ?: 0.0
    val stock = stockInput.trim().toIntOrNull() ?: 10
    viewModelScope.launch {
      _uiState.update { it.copy(isOwnerActionBusy = true, ownerActionFeedback = null) }
      val res =
        repository.createShopifyProductAsOwner(
          config = config,
          title = title,
          price = price,
          inventoryQuantity = stock,
          categoryTitle = categoryTitle,
          description = description,
        )
      res.fold(
        onSuccess = { created ->
          _uiState.update {
            it.copy(
              isOwnerActionBusy = false,
              ownerActionFeedback = "Published \"${created.title}\" to Shopify!",
            )
          }
          onSuccessReset()
          syncShopifyCatalog(_uiState.value.storeStatus.storeUrl)
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isOwnerActionBusy = false,
              ownerActionFeedback = err.localizedMessage ?: "Could not create product.",
            )
          }
        },
      )
    }
  }

  fun refreshOwnerOrders() {
    val config = _uiState.value.ownerAdminConfig
    if (!config.isConfigured) return
    viewModelScope.launch {
      _uiState.update { it.copy(isOwnerActionBusy = true) }
      val res = repository.fetchShopifyOrdersAsOwner(config)
      res.fold(
        onSuccess = { orders ->
          _uiState.update {
            it.copy(isOwnerActionBusy = false, ownerAdminOrders = orders)
          }
        },
        onFailure = { err ->
          _uiState.update {
            it.copy(
              isOwnerActionBusy = false,
              ownerActionFeedback = err.localizedMessage,
            )
          }
        },
      )
    }
  }

  fun openShopifyAdminWebPortal() {
    val adminHost = _uiState.value.ownerAdminConfig.myShopifyDomain
    val adminUrl =
      if (adminHost.isNotBlank()) "https://$adminHost/admin"
      else "https://admin.shopify.com"
    _uiState.update {
      it.copy(
        isOwnerStudioVisible = false,
        activeWebPortal =
          WebPortalRequest(
            title = "Shopify Owner Admin",
            url = adminUrl,
            isCheckout = false,
          ),
      )
    }
  }

  // Optional Notify Me preferences support
  fun openNotifyMeSheet() {
    _uiState.update { state ->
      val existing = state.registration
      state.copy(
        isNotifySheetVisible = true,
        notifyEmailInput = existing.email,
        notifyNameInput = existing.fullName,
        notifySelectedCategoryIds =
          existing.interestedCategoryIds.ifEmpty { state.categories.map { it.id }.toSet() },
        notifyEmailError = null,
        notifyJustSaved = false,
      )
    }
  }

  fun closeNotifyMeSheet() {
    _uiState.update { it.copy(isNotifySheetVisible = false, notifyEmailError = null) }
  }

  fun onNotifyEmailChanged(value: String) {
    _uiState.update { it.copy(notifyEmailInput = value, notifyEmailError = null) }
  }

  fun onNotifyNameChanged(value: String) {
    _uiState.update { it.copy(notifyNameInput = value) }
  }

  fun toggleNotifyCategorySelection(categoryId: String) {
    _uiState.update { state ->
      val updated = state.notifySelectedCategoryIds.toMutableSet()
      if (!updated.add(categoryId)) updated.remove(categoryId)
      state.copy(notifySelectedCategoryIds = updated)
    }
  }

  fun submitNotifyMe() {
    val state = _uiState.value
    val trimmedEmail = state.notifyEmailInput.trim()
    if (trimmedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
      _uiState.update { it.copy(notifyEmailError = "Please enter a valid email address.") }
      return
    }
    val chosenCategories =
      state.notifySelectedCategoryIds.ifEmpty { state.categories.map { it.id }.toSet() }
    viewModelScope.launch {
      repository.saveNotifyRegistration(
        email = trimmedEmail,
        fullName = state.notifyNameInput,
        categoryIds = chosenCategories,
      )
      _uiState.update { it.copy(notifyJustSaved = true, notifyEmailError = null) }
    }
  }

  fun unsubscribeLaunchAlert() {
    viewModelScope.launch {
      repository.unsubscribeLaunchAlert()
      _uiState.update { it.copy(notifyJustSaved = false, isNotifySheetVisible = false) }
    }
  }
}
