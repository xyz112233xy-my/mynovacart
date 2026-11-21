package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsActive
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LaunchCategory
import com.example.model.NovaCartCatalogBlueprint
import com.example.ui.components.NovaCartLogoEmblem
import com.example.ui.components.NovaCartWordmarkHeader
import com.example.ui.components.PulsingComingSoonPill
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCartTheme
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaGold
import com.example.ui.theme.NovaGoldSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun ComingSoonScreen(
  uiState: NovaCartUiState,
  onVisitWebsiteClick: () -> Unit,
  onShareClick: () -> Unit,
  onCustomizeAlertClick: () -> Unit,
  onCategoryClick: (LaunchCategory) -> Unit,
  onExplorePreviewClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var isNotified by rememberSaveable { mutableStateOf(false) }
  var contentVisible by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(Unit) { contentVisible = true }

  val notifiedStateActive = isNotified || uiState.registration.isSubscribed

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
      // Top Brand Header
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

      // Main Coming Soon Card: NOVA CART Logo, Welcome Message & Notify Me Button
      item {
        AnimatedVisibility(
          visible = contentVisible,
          enter = fadeIn(tween(380)) + slideInVertically(tween(380)) { it / 14 },
        ) {
          Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              NovaCartLogoEmblem(
                size = 72.dp,
                modifier = Modifier.testTag("coming_soon_logo"),
              )

              Surface(shape = RoundedCornerShape(8.dp), color = NovaGoldSoft) {
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
                fontFamily = OutfitFontFamily,
                color = ObsidianInk,
                textAlign = TextAlign.Center,
              )

              Text(
                text = stringResource(R.string.welcome_message),
                style = MaterialTheme.typography.titleMedium,
                color = ObsidianInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("coming_soon_welcome_message"),
              )

              Text(
                text = stringResource(R.string.coming_soon_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
                textAlign = TextAlign.Center,
              )

              Spacer(modifier = Modifier.height(2.dp))

              Button(
                onClick = {
                  val message =
                    if (!isNotified) {
                      isNotified = true
                      "Thanks! We will notify you when NOVA CART launches."
                    } else {
                      "You are already on the NOVA CART launch alert list."
                    }
                  Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                modifier =
                  Modifier.fillMaxWidth()
                    .height(54.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("notify_me_button"),
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = if (notifiedStateActive) EmeraldLive else NovaCoral,
                    contentColor = PureWhite,
                  ),
              ) {
                Icon(
                  imageVector =
                    if (notifiedStateActive) Icons.Outlined.CheckCircle
                    else Icons.Outlined.NotificationsActive,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text =
                    if (notifiedStateActive) "Notified • Launch Alert Active"
                    else stringResource(R.string.cta_notify_me),
                  style = MaterialTheme.typography.labelLarge,
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                OutlinedButton(
                  onClick = onVisitWebsiteClick,
                  modifier =
                    Modifier.weight(1f)
                      .height(48.dp)
                      .minimumInteractiveComponentSize()
                      .testTag("visit_website_button"),
                  shape = RoundedCornerShape(12.dp),
                  border = BorderStroke(1.dp, ObsidianInk),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = stringResource(R.string.brand_website_display),
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
                  border = BorderStroke(1.dp, HairlineBorder),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianInk),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Share",
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }

              TextButton(
                onClick = onCustomizeAlertClick,
                modifier = Modifier.testTag("customize_email_alert_button"),
              ) {
                Text(
                  text = "Add email & category preferences",
                  style = MaterialTheme.typography.labelMedium,
                  color = MutedSlate,
                )
              }
            }
          }
        }
      }

      // Editorial Hero Shopping Banner
      item {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(22.dp))
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
              Box(modifier = Modifier.size(8.dp).background(EmeraldLive, shape = CircleShape))
              Text(
                text = "ANDROID APP LIVE • STORE COMING SOON",
                style = MaterialTheme.typography.labelSmall,
                color = ObsidianInk,
              )
            }
          }
        }
      }

      // Launching Product Categories (No fake products/prices/offers)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.section_categories_title),
                style = MaterialTheme.typography.headlineSmall,
                color = ObsidianInk,
              )
              Text(
                text = stringResource(R.string.section_categories_subtitle),
                style = MaterialTheme.typography.bodySmall,
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
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.categories.chunked(2).forEach { rowItems ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    rowItems.forEach { category ->
                      ComingSoonCategoryCard(
                        category = category,
                        isInterested =
                          uiState.registration.isSubscribed &&
                            uiState.registration.interestedCategoryIds.contains(category.id),
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier.weight(1f),
                      )
                    }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.categories.forEach { category ->
                  ComingSoonCategoryCard(
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

      // Upcoming Shopping Preview Link
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = AlabasterSurface,
          border = BorderStroke(1.dp, HairlineBorder),
          modifier =
            Modifier.fillMaxWidth()
              .clickable(onClick = onExplorePreviewClick)
              .testTag("explore_future_features_card"),
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Upcoming Shopping Features",
                style = MaterialTheme.typography.titleSmall,
                color = ObsidianInk,
              )
              Text(
                text = "Cart, Wishlist, UPI, COD & Shopify Sync — Disabled until launch",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
              )
            }
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
              contentDescription = "See upcoming shopping features",
              tint = ObsidianInk,
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun ComingSoonCategoryCard(
  category: LaunchCategory,
  isInterested: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier.testTag("category_card_${category.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
    border = BorderStroke(1.dp, HairlineBorder),
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = category.containerColor,
        modifier = Modifier.size(48.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = category.icon,
            contentDescription = category.title,
            tint = category.accentColor,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
          text = category.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = ObsidianInk,
        )
        Text(
          text = category.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MutedSlate,
        )
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isInterested) EmeraldSoft else PureWhite,
          border =
            BorderStroke(
              1.dp,
              if (isInterested) EmeraldLive.copy(alpha = 0.25f) else HairlineBorder,
            ),
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

@Preview(showBackground = true)
@Composable
private fun ComingSoonScreenPreview() {
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
