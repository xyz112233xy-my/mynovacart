package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.ui.components.LaunchSplashOverlay
import com.example.ui.components.ProductDetailSheet
import com.example.ui.components.ShopifyWebPortalDialog
import com.example.ui.screens.AccountStoreScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ConciergeScreen
import com.example.ui.screens.ShopHomeScreen
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCartTheme
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartViewModel
import com.example.ui.viewmodel.NovaConciergeViewModel

object NovaCartRoutes {
  const val HOME = "home"
  const val CATALOG = "catalog"
  const val CART = "cart"
  const val CONCIERGE = "concierge"
  const val ACCOUNT = "account"
}

private data class NavDestination(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val bottomDestinations =
  listOf(
    NavDestination(
      route = NovaCartRoutes.HOME,
      label = "Home",
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home,
      testTag = "nav_home",
    ),
    NavDestination(
      route = NovaCartRoutes.CATALOG,
      label = "Shop",
      selectedIcon = Icons.Filled.Storefront,
      unselectedIcon = Icons.Outlined.Storefront,
      testTag = "nav_shop",
    ),
    NavDestination(
      route = NovaCartRoutes.CART,
      label = "Cart",
      selectedIcon = Icons.Filled.ShoppingCart,
      unselectedIcon = Icons.Outlined.ShoppingCart,
      testTag = "nav_cart",
    ),
    NavDestination(
      route = NovaCartRoutes.CONCIERGE,
      label = "Concierge",
      selectedIcon = Icons.Filled.AutoAwesome,
      unselectedIcon = Icons.Outlined.AutoAwesome,
      testTag = "nav_concierge",
    ),
    NavDestination(
      route = NovaCartRoutes.ACCOUNT,
      label = "Account",
      selectedIcon = Icons.Filled.AccountCircle,
      unselectedIcon = Icons.Outlined.AccountCircle,
      testTag = "nav_account",
    ),
  )

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Configure Firebase App Check with launch intent before any UI or ViewModels
    configureAppCheck(this, intent)

    enableEdgeToEdge()
    setContent {
      NovaCartTheme {
        NovaCartAppRoot()
      }
    }
  }
}

@Composable
fun NovaCartAppRoot(
  viewModel: NovaCartViewModel = viewModel(),
  conciergeViewModel: NovaConciergeViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val conciergeUiState by conciergeViewModel.uiState.collectAsStateWithLifecycle()
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: NovaCartRoutes.HOME
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.statusMessageBanner) {
    val msg = uiState.statusMessageBanner
    if (!msg.isNullOrBlank()) {
      snackbarHostState.showSnackbar(msg)
      viewModel.consumeStatusBanner()
    }
  }

  val shareNovaCart: () -> Unit = {
    val shareIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "NOVA CART — Official Store")
        putExtra(
          Intent.EXTRA_TEXT,
          "Shop Home Decor, Kitchen Accessories, Beauty Care, Daily Life Gadgets & Kids on NOVA CART: ${uiState.storeStatus.storeUrl}",
        )
      }
    runCatching {
      context.startActivity(Intent.createChooser(shareIntent, "Share NOVA CART"))
    }
  }

  val navigateToRoute: (String) -> Unit = { route ->
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = PureWhite,
      contentWindowInsets = WindowInsets.safeDrawing,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      bottomBar = {
        if (!useNavigationRail) {
          Box {
            HorizontalDivider(color = HairlineBorder, thickness = 1.dp)
            NavigationBar(containerColor = PureWhite, tonalElevation = 0.dp) {
              bottomDestinations.forEach { dest ->
                val selected = currentRoute == dest.route
                NavigationBarItem(
                  selected = selected,
                  onClick = { navigateToRoute(dest.route) },
                  icon = {
                    NavDestinationIcon(
                      destination = dest,
                      selected = selected,
                      cartItemCount = uiState.cartItemCount,
                    )
                  },
                  label = {
                    Text(
                      text = dest.label,
                      style = MaterialTheme.typography.labelSmall,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor = NovaCoral,
                      selectedTextColor = ObsidianInk,
                      indicatorColor = NovaCoralSoft,
                      unselectedIconColor = MutedSlate,
                      unselectedTextColor = MutedSlate,
                    ),
                  modifier = Modifier.testTag(dest.testTag),
                )
              }
            }
          }
        }
      },
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        if (useNavigationRail) {
          NavigationRail(containerColor = PureWhite) {
            bottomDestinations.forEach { dest ->
              val selected = currentRoute == dest.route
              NavigationRailItem(
                selected = selected,
                onClick = { navigateToRoute(dest.route) },
                icon = {
                  NavDestinationIcon(
                    destination = dest,
                    selected = selected,
                    cartItemCount = uiState.cartItemCount,
                  )
                },
                label = {
                  Text(
                    text = dest.label,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                  )
                },
                colors =
                  NavigationRailItemDefaults.colors(
                    selectedIconColor = NovaCoral,
                    selectedTextColor = ObsidianInk,
                    indicatorColor = NovaCoralSoft,
                    unselectedIconColor = MutedSlate,
                    unselectedTextColor = MutedSlate,
                  ),
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }

        NavHost(
          navController = navController,
          startDestination = NovaCartRoutes.HOME,
          modifier = Modifier.weight(1f).fillMaxSize(),
        ) {
          composable(NovaCartRoutes.HOME) {
            ShopHomeScreen(
              uiState = uiState,
              onSearchBarClick = { navigateToRoute(NovaCartRoutes.CATALOG) },
              onShopNowClick = {
                viewModel.selectCategoryFilter(null)
                navigateToRoute(NovaCartRoutes.CATALOG)
              },
              onCategoryClick = { category ->
                viewModel.selectCategoryFilter(category.id)
                navigateToRoute(NovaCartRoutes.CATALOG)
              },
              onProductClick = viewModel::openProductDetail,
              onToggleWishlist = viewModel::toggleWishlist,
              onQuickAddProduct = { product -> viewModel.addToCart(product) },
              onRefreshStore = viewModel::refreshStoreCatalog,
              onOpenWebsitePortal = viewModel::openStorefrontWebsite,
              onOpenStoreSettings = { navigateToRoute(NovaCartRoutes.ACCOUNT) },
              onOpenOrdersPortal = viewModel::openOrdersPortal,
            )
          }

          composable(NovaCartRoutes.CATALOG) {
            CatalogScreen(
              uiState = uiState,
              onSearchQueryChanged = viewModel::onSearchQueryChanged,
              onSelectCategory = viewModel::selectCategoryFilter,
              onToggleWishlistFilter = viewModel::toggleWishlistFilter,
              onSelectSort = viewModel::selectSortOption,
              onProductClick = viewModel::openProductDetail,
              onToggleWishlist = viewModel::toggleWishlist,
              onQuickAddProduct = { product -> viewModel.addToCart(product) },
              onSyncStore = viewModel::refreshStoreCatalog,
              onOpenStoreSettings = { navigateToRoute(NovaCartRoutes.ACCOUNT) },
            )
          }

          composable(NovaCartRoutes.CART) {
            CartScreen(
              uiState = uiState,
              onUpdateQuantity = viewModel::updateCartQuantity,
              onRemoveItem = viewModel::removeFromCart,
              onProceedToCheckout = viewModel::startShopifyCheckout,
              onBrowseProducts = { navigateToRoute(NovaCartRoutes.CATALOG) },
            )
          }

          composable(NovaCartRoutes.CONCIERGE) {
            ConciergeScreen(
              uiState = conciergeUiState,
              onPromptChanged = conciergeViewModel::onPromptChanged,
              onSendPrompt = { conciergeViewModel.sendPrompt() },
              onQuickPromptClick = conciergeViewModel::sendQuickPrompt,
              onClearClick = conciergeViewModel::clearConversation,
            )
          }

          composable(NovaCartRoutes.ACCOUNT) {
            AccountStoreScreen(
              uiState = uiState,
              onStoreUrlChanged = viewModel::onStoreUrlInputChanged,
              onSaveAndSyncStore = viewModel::saveAndSyncStoreUrl,
              onOpenCustomerAccount = viewModel::openCustomerAccountPortal,
              onOpenOrdersPortal = viewModel::openOrdersPortal,
              onOpenWebsitePortal = viewModel::openStorefrontWebsite,
              onReopenOrderUrl = viewModel::reopenOrderUrl,
              onShareApp = shareNovaCart,
            )
          }
        }
      }
    }

    val selectedProduct = uiState.selectedProductForDetail
    ProductDetailSheet(
      product = selectedProduct,
      isWishlisted =
        selectedProduct != null && uiState.wishlistIds.contains(selectedProduct.id.toString()),
      onToggleWishlist = viewModel::toggleWishlist,
      onAddToCart = viewModel::addToCart,
      onBuyNow = viewModel::buyNowSingleProduct,
      onDismiss = viewModel::closeProductDetail,
    )

    ShopifyWebPortalDialog(
      portalRequest = uiState.activeWebPortal,
      onOrderCompleted = viewModel::onShopifyOrderCompleted,
      onDismiss = viewModel::closeWebPortal,
    )

    LaunchSplashOverlay(
      visible = uiState.showLaunchSplash,
      onDismiss = viewModel::dismissLaunchSplash,
    )
  }
}

@Composable
private fun NavDestinationIcon(
  destination: NavDestination,
  selected: Boolean,
  cartItemCount: Int,
) {
  val iconVector = if (selected) destination.selectedIcon else destination.unselectedIcon
  if (destination.route == NovaCartRoutes.CART && cartItemCount > 0) {
    BadgedBox(
      badge = {
        Badge(containerColor = NovaCoral, contentColor = PureWhite) {
          Text(if (cartItemCount > 99) "99+" else cartItemCount.toString())
        }
      }
    ) {
      Icon(imageVector = iconVector, contentDescription = destination.label)
    }
  } else {
    Icon(imageVector = iconVector, contentDescription = destination.label)
  }
}
