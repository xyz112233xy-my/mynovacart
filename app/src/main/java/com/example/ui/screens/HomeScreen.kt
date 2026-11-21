package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LaunchCategory
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.NovaCartWordmarkHeader
import com.example.ui.components.PulsingComingSoonPill
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.NovaGold
import com.example.ui.theme.NovaGoldSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun HomeScreen(
  uiState: NovaCartUiState,
  onNotifyMeClick: () -> Unit,
  onVisitWebsiteClick: () -> Unit,
  onShareClick: () -> Unit,
  onCategoryClick: (LaunchCategory) -> Unit,
  onExplorePreviewClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var contentVisible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) { contentVisible = true }

  Box(modifier = modifier.fillMaxSize().background(PureWhite), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
      // 1. Editorial Brand Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          NovaCartWordmarkHeader()
          PulsingComingSoonPill(label = stringResource(R.string.coming_soon_badge))
        }
      }

      // 2. Hero Banner & Coming Soon Announcement Card
      item {
        AnimatedVisibility(
          visible = contentVisible,
          enter = fadeIn(tween(420)) + slideInVertically(tween(420)) { it / 12 },
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(24.dp))
                  .background(AlabasterSurface),
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "NOVA CART curated lifestyle shopping banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
              )
              Box(
                modifier =
                  Modifier.matchParentSize()
                    .background(
                      Brush.verticalGradient(
                        colors =
                          listOf(
                            Color.Transparent,
                            Color.Transparent,
                            ObsidianInk.copy(alpha = 0.55f),
                          )
                      )
                    )
              )
              Surface(
                shape = CircleShape,
                color = PureWhite.copy(alpha = 0.94f),
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Box(
                    modifier =
                      Modifier.size(8.dp).background(EmeraldLive, shape = CircleShape)
                  )
                  Text(
                    text = "ANDROID APP LIVE • STORE COMING SOON",
                    style = MaterialTheme.typography.labelSmall,
                    color = ObsidianInk,
                  )
                }
              }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = NovaGoldSoft,
              ) {
                Text(
                  text = "OFFICIAL LAUNCH PREVIEW",
                  style = MaterialTheme.typography.labelSmall,
                  color = NovaGold,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
              }

              Text(
                text = stringResource(R.string.coming_soon_headline),
                style = MaterialTheme.typography.displaySmall,
                color = ObsidianInk,
              )

              Text(
                text = stringResource(R.string.welcome_message),
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
              )

              Text(
                text = stringResource(R.string.coming_soon_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MutedSlate,
              )
            }

            // Primary CTAs: Notify Me + Visit Website
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Button(
                onClick = onNotifyMeClick,
                modifier =
                  Modifier.weight(1f)
                    .height(54.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("notify_me_button"),
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor =
                      if (uiState.registration.isSubscribed) EmeraldLive else NovaCoral,
                    contentColor = PureWhite,
                  ),
              ) {
                Icon(
                  imageVector =
                    if (uiState.registration.isSubscribed) Icons.Outlined.CheckCircle
                    else Icons.Outlined.NotificationsActive,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text =
                    if (uiState.registration.isSubscribed) "Alert Saved"
                    else stringResource(R.string.cta_notify_me),
                  style = MaterialTheme.typography.labelLarge,
                )
              }

              OutlinedButton(
                onClick = onVisitWebsiteClick,
                modifier =
                  Modifier.weight(1f)
                    .height(54.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("visit_website_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, ObsidianInk),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Language,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = stringResource(R.string.brand_website_display),
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }
          }
        }
      }

      // 3. Launching Product Categories (No fake products/prices/offers)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.section_categories_title),
                style = MaterialTheme.typography.headlineMedium,
                color = ObsidianInk,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = stringResource(R.string.section_categories_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
              )
            }
            Text(
              text = "5 Departments",
              style = MaterialTheme.typography.labelMedium,
              color = NovaCoral,
            )
          }

          BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val useTwoColumns = maxWidth >= 560.dp
            if (useTwoColumns) {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.categories.chunked(2).forEach { rowItems ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                  ) {
                    rowItems.forEach { category ->
                      CategoryPreviewCard(
                        category = category,
                        isInterested =
                          uiState.registration.isSubscribed &&
                            uiState.registration.interestedCategoryIds.contains(category.id),
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier.weight(1f),
                      )
                    }
                    if (rowItems.size == 1) {
                      Spacer(modifier = Modifier.weight(1f))
                    }
                  }
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.categories.forEach { category ->
                  CategoryPreviewCard(
                    category = category,
                    isInterested =
                      uiState.registration.isSubscribed &&
                        uiState.registration.interestedCategoryIds.contains(category.id),
                    onClick = { onCategoryClick(category) },
                    modifier = Modifier.fillMaxWidth(),
                  )
                }
              }
            }
          }
        }
      }

      // 4. Official Launch Announcement Showcase
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
          border = BorderStroke(1.dp, HairlineBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column {
            Image(
              painter = painterResource(id = R.drawable.img_launch_showcase),
              contentDescription = "NOVA CART upcoming storefront showcase",
              contentScale = ContentScale.Crop,
              modifier =
                Modifier.fillMaxWidth()
                  .aspectRatio(4f / 3f)
                  .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            )

            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Campaign,
                  contentDescription = null,
                  tint = NovaCoral,
                  modifier = Modifier.size(20.dp),
                )
                Text(
                  text = stringResource(R.string.section_launch_announcement_title),
                  style = MaterialTheme.typography.titleLarge,
                  color = ObsidianInk,
                )
              }

              Text(
                text =
                  "The NOVA CART Android app is launching today so early shoppers can reserve launch alerts and preview our upcoming store experience. Full product browsing, cart, and Shopify checkout via mynovacart.in activate automatically on launch day.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
              )

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = PureWhite,
                border = BorderStroke(1.dp, HairlineBorder),
                modifier =
                  Modifier.fillMaxWidth()
                    .clickable(onClick = onExplorePreviewClick)
                    .testTag("explore_future_features_card"),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "See Upcoming Shopping Features",
                      style = MaterialTheme.typography.titleSmall,
                      color = ObsidianInk,
                    )
                    Text(
                      text = "Cart, Wishlist, UPI, COD & Order Tracking — Disabled until launch",
                      style = MaterialTheme.typography.bodySmall,
                      color = MutedSlate,
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "Explore upcoming shopping features",
                    tint = ObsidianInk,
                  )
                }
              }
            }
          }
        }
      }

      // 5. Website & Social Connect Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = ObsidianInk),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = stringResource(R.string.section_social_title),
                style = MaterialTheme.typography.headlineSmall,
                color = PureWhite,
              )
              Surface(
                shape = CircleShape,
                color = PureWhite.copy(alpha = 0.14f),
              ) {
                Text(
                  text = "OFFICIAL",
                  style = MaterialTheme.typography.labelSmall,
                  color = PureWhite,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }
            }

            Text(
              text =
                "Bookmark our official store portal and share NOVA CART with friends. Social media channels will appear here automatically once published on mynovacart.in.",
              style = MaterialTheme.typography.bodyMedium,
              color = PureWhite.copy(alpha = 0.78f),
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = onVisitWebsiteClick,
                modifier =
                  Modifier.weight(1f)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("footer_website_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = ObsidianInk),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Public,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = NovaCartCatalogBlueprint.OFFICIAL_DOMAIN_LABEL,
                  style = MaterialTheme.typography.labelLarge,
                )
              }

              OutlinedButton(
                onClick = onShareClick,
                modifier =
                  Modifier.weight(1f)
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("share_app_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
              ) {
                Icon(
                  imageVector = Icons.Outlined.Share,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Share App",
                  style = MaterialTheme.typography.labelLarge,
                )
              }
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun CategoryPreviewCard(
  category: LaunchCategory,
  isInterested: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.testTag("category_card_${category.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
    border = BorderStroke(1.dp, HairlineBorder),
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = category.containerColor,
        modifier = Modifier.size(52.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = category.icon,
            contentDescription = category.title,
            tint = category.accentColor,
            modifier = Modifier.size(26.dp),
          )
        }
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = ObsidianInk,
          )
        }
        Text(
          text = category.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MutedSlate,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isInterested) EmeraldSoft else PureWhite,
          border = BorderStroke(1.dp, if (isInterested) EmeraldLive.copy(alpha = 0.25f) else HairlineBorder),
        ) {
          Text(
            text = if (isInterested) "Launch Alert On" else "Coming at Launch",
            style = MaterialTheme.typography.labelSmall,
            color = if (isInterested) EmeraldLive else MutedSlate,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          )
        }
      }

      Icon(
        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
        contentDescription = "Preview ${category.title}",
        tint = MutedSlate,
        modifier = Modifier.size(18.dp),
      )
    }
  }
}
