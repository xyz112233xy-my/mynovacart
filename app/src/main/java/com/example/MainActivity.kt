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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ai.configureAppCheck
import com.example.model.FutureFeature
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.CategoryPreviewSheet
import com.example.ui.components.LaunchSplashOverlay
import com.example.ui.components.NotifyMeBottomSheet
import com.example.ui.screens.ComingSoonScreen
import com.example.ui.screens.ConciergeScreen
import com.example.ui.screens.LaunchHubScreen
import com.example.ui.screens.StorePreviewScreen
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
  const val STORE_PREVIEW = "store_preview"
  const val CONCIERGE = "concierge"
  const val LAUNCH_HUB = "launch_hub"
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
      route = NovaCartRoutes.STORE_PREVIEW,
      label = "Store",
      selectedIcon = Icons.Filled.Storefront,
      unselectedIcon = Icons.Outlined.Storefront,
      testTag = "nav_store",
    ),
    NavDestination(
      route = NovaCartRoutes.CONCIERGE,
      label = "Concierge",
      selectedIcon = Icons.Filled.AutoAwesome,
      unselectedIcon = Icons.Outlined.AutoAwesome,
      testTag = "nav_concierge",
    ),
    NavDestination(
      route = NovaCartRoutes.LAUNCH_HUB,
      label = "Launch Hub",
      selectedIcon = Icons.Filled.CloudSync,
      unselectedIcon = Icons.Outlined.CloudSync,
      testTag = "nav_launch_hub",
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

  val openOfficialWebsite: () -> Unit = {
    val intent =
      Intent(Intent.ACTION_VIEW, Uri.parse(NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    runCatching { context.startActivity(intent) }
  }

  val shareNovaCart: () -> Unit = {
    val shareIntent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "NOVA CART — Coming Soon")
        putExtra(
          Intent.EXTRA_TEXT,
          "Welcome to NOVA CART – Your Next Favourite Shopping Destination! Launching soon at ${NovaCartCatalogBlueprint.OFFICIAL_WEBSITE_URL}",
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
                    Icon(
                      imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                      contentDescription = dest.label,
                    )
                  },
                  label = { Text(dest.label, style = MaterialTheme.typography.labelMedium) },
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
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = dest.label,
                  )
                },
                label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
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
            ComingSoonScreen(
              uiState = uiState,
              onVisitWebsiteClick = openOfficialWebsite,
              onShareClick = shareNovaCart,
              onCustomizeAlertClick = { viewModel.openNotifyMeSheet() },
              onCategoryClick = viewModel::openCategoryPreview,
              onExplorePreviewClick = { navigateToRoute(NovaCartRoutes.STORE_PREVIEW) },
            )
          }

          composable(NovaCartRoutes.STORE_PREVIEW) {
            StorePreviewScreen(
              uiState = uiState,
              onInspectLockedFeature = viewModel::inspectLockedFeature,
              onNotifyMeClick = { viewModel.openNotifyMeSheet() },
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

          composable(NovaCartRoutes.LAUNCH_HUB) {
            LaunchHubScreen(
              uiState = uiState,
              onCheckStatusClick = viewModel::refreshStoreLaunchStatus,
              onNotifyMeClick = { viewModel.openNotifyMeSheet() },
              onVisitWebsiteClick = openOfficialWebsite,
              onShareClick = shareNovaCart,
            )
          }
        }
      }
    }

    NotifyMeBottomSheet(
      visible = uiState.isNotifySheetVisible,
      email = uiState.notifyEmailInput,
      fullName = uiState.notifyNameInput,
      emailError = uiState.notifyEmailError,
      categories = uiState.categories,
      selectedCategoryIds = uiState.notifySelectedCategoryIds,
      isAlreadySubscribed = uiState.registration.isSubscribed,
      justSaved = uiState.notifyJustSaved,
      onEmailChange = viewModel::onNotifyEmailChanged,
      onNameChange = viewModel::onNotifyNameChanged,
      onToggleCategory = viewModel::toggleNotifyCategorySelection,
      onSubmit = viewModel::submitNotifyMe,
      onUnsubscribe = viewModel::unsubscribeLaunchAlert,
      onDismiss = viewModel::closeNotifyMeSheet,
    )

    CategoryPreviewSheet(
      category = uiState.selectedCategoryForPreview,
      isSubscribedToApp = uiState.registration.isSubscribed,
      isInterestedInCategory =
        uiState.selectedCategoryForPreview?.let {
          uiState.registration.interestedCategoryIds.contains(it.id)
        } ?: false,
      onToggleCategoryAlert = viewModel::toggleCategoryAlertFromPreview,
      onDismiss = viewModel::closeCategoryPreview,
    )

    uiState.inspectedLockedFeature?.let { feature ->
      LockedFeatureDialog(
        feature = feature,
        onNotifyMe = {
          viewModel.dismissLockedFeatureDialog()
          viewModel.openNotifyMeSheet()
        },
        onDismiss = viewModel::dismissLockedFeatureDialog,
      )
    }

    LaunchSplashOverlay(
      visible = uiState.showLaunchSplash,
      onDismiss = viewModel::dismissLaunchSplash,
    )
  }
}

@Composable
private fun LockedFeatureDialog(
  feature: FutureFeature,
  onNotifyMe: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = PureWhite,
    icon = {
      Icon(
        imageVector = Icons.Outlined.Lock,
        contentDescription = "Feature disabled until launch",
        tint = NovaCoral,
      )
    },
    title = {
      Text(
        text = "${feature.title} • Coming at Launch",
        style = MaterialTheme.typography.titleLarge,
        color = ObsidianInk,
      )
    },
    text = {
      Text(
        text =
          "${feature.description}\n\nIntegration ready: ${feature.shopifyBridge}. This feature stays disabled until mynovacart.in officially launches.",
        style = MaterialTheme.typography.bodyMedium,
        color = MutedSlate,
      )
    },
    confirmButton = {
      Button(
        onClick = onNotifyMe,
        colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
      ) {
        Text("Notify Me")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = ObsidianInk)
      }
    },
  )
}
