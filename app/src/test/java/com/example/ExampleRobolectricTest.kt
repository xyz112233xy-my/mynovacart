package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.NovaCartRepository
import com.example.model.CartItem
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.OwnerPinGateDialog
import com.example.ui.screens.AccountStoreScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.ConciergeScreen
import com.example.ui.screens.ShopHomeScreen
import com.example.ui.theme.NovaCartTheme
import com.example.ui.viewmodel.NovaCartUiState
import com.example.ui.viewmodel.NovaConciergeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `app name matches Nova Cart branding`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nova Cart", appName)
  }

  @Test
  fun `official launch categories match specification`() {
    val titles = NovaCartCatalogBlueprint.launchCategories.map { it.title }
    assertEquals(
      listOf(
        "Home Decor",
        "Kitchen Accessories",
        "Beauty Care",
        "Daily Life Gadgets",
        "Kids",
      ),
      titles,
    )
  }

  @Test
  fun `all ten shopping capabilities are unlocked`() {
    val features = NovaCartCatalogBlueprint.futureShoppingFeatures
    assertEquals(10, features.size)
    assertTrue(features.all { it.isEnabled })
    assertEquals("https://mynovacart.in", NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL)
  }

  @Test
  fun `shopify checkout permalink builds valid cart url`() {
    val items =
      listOf(
        CartItem(
          productId = 1L,
          variantId = 101L,
          title = "Ceramic Vase",
          variantTitle = "Ivory",
          priceAmount = 799.0,
          imageUrl = null,
          quantity = 2,
        ),
        CartItem(
          productId = 2L,
          variantId = 202L,
          title = "Desk Lamp",
          variantTitle = "Warm",
          priceAmount = 1299.0,
          imageUrl = null,
          quantity = 1,
        ),
      )

    val url =
      NovaCartRepository.buildShopifyCheckoutUrl(
        storeUrl = "https://mynovacart.in/",
        items = items,
        customerEmail = "shopper@example.com",
      )

    assertTrue(url.startsWith("https://mynovacart.in/cart/101:2,202:1"))
    assertTrue(url.contains("note="))
    assertTrue(url.contains("shopper%40example.com"))
  }

  @Test
  fun `owner shopify admin host validator normalizes valid hosts and rejects loopback`() {
    assertEquals(
      "mynovacart.myshopify.com",
      NovaCartRepository.normalizeShopifyAdminHost("https://mynovacart.myshopify.com/admin"),
    )
    assertNull(NovaCartRepository.normalizeShopifyAdminHost("http://localhost:8080"))
    assertNull(NovaCartRepository.normalizeShopifyAdminHost("127.0.0.1"))
    assertNull(NovaCartRepository.normalizeShopifyAdminHost("not a host"))
  }

  @Test
  fun `shop home screen displays search bar departments without store url setup`() {
    composeTestRule.setContent {
      NovaCartTheme {
        ShopHomeScreen(
          uiState = NovaCartUiState(categories = NovaCartCatalogBlueprint.launchCategories),
          onSearchBarClick = {},
          onShopNowClick = {},
          onCategoryClick = {},
          onProductClick = {},
          onToggleWishlist = {},
          onQuickAddProduct = {},
          onRefreshStore = {},
          onOpenWebsitePortal = {},
          onOpenOrdersPortal = {},
        )
      }
    }

    composeTestRule.onNodeWithTag("home_search_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_shop_now_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_category_home_decor").assertIsDisplayed()
    composeTestRule.onNodeWithText("Shop by Department").assertIsDisplayed()
    composeTestRule.onNodeWithText("Store URL").assertDoesNotExist()
  }

  @Test
  fun `customer account screen hides store url setup and renders portal actions`() {
    composeTestRule.setContent {
      NovaCartTheme {
        AccountStoreScreen(
          uiState = NovaCartUiState(),
          onOpenCustomerAccount = {},
          onOpenOrdersPortal = {},
          onOpenWebsitePortal = {},
          onReopenOrderUrl = { _, _ -> },
          onShareApp = {},
          onOpenOwnerStudio = {},
        )
      }
    }

    composeTestRule.onNodeWithTag("open_customer_account_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("open_orders_portal_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Shopify Website Connection").assertDoesNotExist()
    composeTestRule
      .onNodeWithTag("account_lazy_list")
      .performScrollToNode(hasTestTag("account_version_footer"))
    composeTestRule.onNodeWithTag("account_version_footer").assertIsDisplayed()
  }

  @Test
  fun `owner pin gate shows create pin dialog on first run`() {
    composeTestRule.setContent {
      NovaCartTheme {
        OwnerPinGateDialog(
          visible = true,
          isPinSet = false,
          errorText = null,
          onSubmitPin = {},
          onDismiss = {},
        )
      }
    }

    composeTestRule.onNodeWithText("Create Owner PIN").assertIsDisplayed()
    composeTestRule.onNodeWithTag("owner_pin_input").assertIsDisplayed()
    composeTestRule.onNodeWithTag("owner_pin_submit_button").assertIsDisplayed()
  }

  @Test
  fun `cart screen displays checkout button when items exist`() {
    val cartItem =
      CartItem(
        productId = 1L,
        variantId = 101L,
        title = "Ceramic Vase",
        variantTitle = "Ivory",
        priceAmount = 799.0,
        imageUrl = null,
        quantity = 2,
      )
    composeTestRule.setContent {
      NovaCartTheme {
        CartScreen(
          uiState = NovaCartUiState(cartItems = listOf(cartItem)),
          onUpdateQuantity = { _, _ -> },
          onRemoveItem = {},
          onProceedToCheckout = {},
          onBrowseProducts = {},
        )
      }
    }

    composeTestRule.onNodeWithText("Ceramic Vase").assertIsDisplayed()
    composeTestRule.onNodeWithTag("proceed_to_checkout_button").assertIsDisplayed()
  }

  @Test
  fun `concierge screen renders starter prompts and composer`() {
    composeTestRule.setContent {
      NovaCartTheme {
        ConciergeScreen(
          uiState = NovaConciergeUiState(),
          onPromptChanged = {},
          onSendPrompt = {},
          onQuickPromptClick = {},
          onClearClick = {},
        )
      }
    }

    composeTestRule.onNodeWithText("NOVA Concierge").assertIsDisplayed()
    composeTestRule.onNodeWithTag("concierge_chip_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("concierge_prompt_input").assertIsDisplayed()
  }
}
