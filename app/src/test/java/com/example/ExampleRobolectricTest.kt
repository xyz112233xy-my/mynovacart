package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.screens.ComingSoonScreen
import com.example.ui.theme.NovaCartTheme
import com.example.ui.viewmodel.NovaCartUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

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
  fun `future shopping features remain disabled until launch`() {
    val features = NovaCartCatalogBlueprint.futureShoppingFeatures
    assertEquals(10, features.size)
    assertTrue(features.all { !it.isEnabled })
    assertFalse(features.any { it.isEnabled })
    assertEquals("https://mynovacart.in", NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL)
  }

  @Test
  fun `coming soon screen displays logo welcome message and notify me shows toast`() {
    composeTestRule.setContent {
      NovaCartTheme {
        ComingSoonScreen(
          uiState = NovaCartUiState(categories = NovaCartCatalogBlueprint.launchCategories),
          onVisitWebsiteClick = {},
          onShareClick = {},
          onCustomizeAlertClick = {},
          onCategoryClick = {},
          onExplorePreviewClick = {},
        )
      }
    }

    composeTestRule.onNodeWithTag("coming_soon_logo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("coming_soon_welcome_message").assertIsDisplayed()
    composeTestRule.onNodeWithText("Notify Me").assertIsDisplayed()

    composeTestRule.onNodeWithTag("notify_me_button").performClick()

    assertEquals(
      "Thanks! We will notify you when NOVA CART launches.",
      ShadowToast.getTextOfLatestToast(),
    )
    composeTestRule.onNodeWithText("Notified • Launch Alert Active").assertIsDisplayed()
  }

  @Test
  fun `concierge screen renders starter prompts and composer`() {
    composeTestRule.setContent {
      NovaCartTheme {
        com.example.ui.screens.ConciergeScreen(
          uiState = com.example.ui.viewmodel.NovaConciergeUiState(),
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
