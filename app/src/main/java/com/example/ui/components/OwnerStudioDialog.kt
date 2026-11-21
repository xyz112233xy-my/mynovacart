package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NovaCartUiState

@Composable
fun OwnerPinGateDialog(
  visible: Boolean,
  isPinSet: Boolean,
  errorText: String?,
  onSubmitPin: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (!visible) return

  var pinInput by remember(visible) { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = PureWhite,
    icon = {
      Icon(
        imageVector = Icons.Outlined.AdminPanelSettings,
        contentDescription = "Owner Studio Access",
        tint = NovaCoral,
      )
    },
    title = {
      Text(
        text = if (isPinSet) "Owner Studio Access" else "Create Owner PIN",
        style = MaterialTheme.typography.titleLarge,
        color = ObsidianInk,
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text =
            if (isPinSet) {
              "Enter your Owner PIN to manage Shopify products, orders, and storefront settings."
            } else {
              "Set a private Owner PIN on this device to unlock Store Admin, product publishing, and order management."
            },
          style = MaterialTheme.typography.bodyMedium,
          color = MutedSlate,
        )
        OutlinedTextField(
          value = pinInput,
          onValueChange = { if (it.length <= 12) pinInput = it },
          label = { Text(if (isPinSet) "Owner PIN" else "New Owner PIN (4+ digits)") },
          visualTransformation = PasswordVisualTransformation(),
          singleLine = true,
          keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
          isError = errorText != null,
          supportingText = if (errorText != null) { { Text(errorText) } } else null,
          modifier = Modifier.fillMaxWidth().testTag("owner_pin_input"),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSubmitPin(pinInput) },
        colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
        modifier = Modifier.testTag("owner_pin_submit_button"),
      ) {
        Text(if (isPinSet) "Unlock" else "Save PIN & Open")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = ObsidianInk)
      }
    },
  )
}

@Composable
fun OwnerStudioDialog(
  uiState: NovaCartUiState,
  onStoreUrlChanged: (String) -> Unit,
  onSaveAndSyncStore: () -> Unit,
  onAdminDomainChanged: (String) -> Unit,
  onAdminTokenChanged: (String) -> Unit,
  onConnectAdminApi: () -> Unit,
  onDisconnectAdminApi: () -> Unit,
  onCreateProduct: (
    title: String,
    price: String,
    stock: String,
    categoryTitle: String,
    description: String,
    onSuccessReset: () -> Unit,
  ) -> Unit,
  onRefreshOrders: () -> Unit,
  onOpenFullShopifyAdmin: () -> Unit,
  onDismiss: () -> Unit,
) {
  if (!uiState.isOwnerStudioVisible) return

  var productTitle by remember { mutableStateOf("") }
  var productPrice by remember { mutableStateOf("") }
  var productStock by remember { mutableStateOf("25") }
  var productDesc by remember { mutableStateOf("") }
  var selectedCategoryTitle by remember {
    mutableStateOf(uiState.categories.firstOrNull()?.title ?: "Home Decor")
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier = Modifier.fillMaxSize().systemBarsPadding(),
      color = PureWhite,
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = NovaCoral)
            Text(
              text = "Owner Studio",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = ObsidianInk,
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("owner_studio_close")) {
            Icon(Icons.Outlined.Close, contentDescription = "Lock Owner Studio", tint = ObsidianInk)
          }
        }

        HorizontalDivider(color = HairlineBorder)

        Column(
          modifier =
            Modifier.weight(1f)
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
              .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
          if (!uiState.ownerActionFeedback.isNullOrBlank()) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = EmeraldSoft,
              border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.25f)),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = uiState.ownerActionFeedback,
                style = MaterialTheme.typography.bodySmall,
                color = ObsidianInk,
                modifier = Modifier.padding(12.dp),
              )
            }
          }

          // 1. Full Shopify Admin Console inside app
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianInk),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Text(
                text = "Full Shopify Admin Dashboard",
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite,
              )
              Text(
                text =
                  "Sign in with your Shopify Owner account inside the app to add products with photos, edit inventory, fulfill orders, set discounts, and manage your whole website.",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.78f),
              )
              Button(
                onClick = onOpenFullShopifyAdmin,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("owner_open_shopify_admin"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
              ) {
                Icon(Icons.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Shopify Admin in App", style = MaterialTheme.typography.labelLarge)
              }
            }
          }

          // 2. Customer App Pre-Connected Storefront Settings
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(Icons.Outlined.CloudSync, contentDescription = null, tint = NovaCoral)
                Text(
                  text = "Pre-Connected Storefront",
                  style = MaterialTheme.typography.titleMedium,
                  color = ObsidianInk,
                )
              }
              Text(
                text = uiState.storeStatus.statusDetail,
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
              )
              OutlinedTextField(
                value = uiState.storeUrlInput,
                onValueChange = onStoreUrlChanged,
                label = { Text("Customer Storefront URL") },
                placeholder = { Text("https://mynovacart.in") },
                singleLine = true,
                keyboardOptions =
                  KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                shape = RoundedCornerShape(12.dp),
                colors =
                  OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite,
                  ),
                modifier = Modifier.fillMaxWidth().testTag("owner_store_url_input"),
              )
              Button(
                onClick = onSaveAndSyncStore,
                enabled = !uiState.storeStatus.isChecking,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
              ) {
                if (uiState.storeStatus.isChecking) {
                  CircularProgressIndicator(color = PureWhite, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Sync Live Catalog")
              }
            }
          }

          // 3. Device-Local Owner Admin API (Quick Add Product & Orders)
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(Icons.Outlined.Lock, contentDescription = null, tint = EmeraldLive)
                  Text(
                    text = "Owner API Connection",
                    style = MaterialTheme.typography.titleMedium,
                    color = ObsidianInk,
                  )
                }
                Surface(
                  shape = RoundedCornerShape(99.dp),
                  color = if (uiState.ownerAdminConfig.isConfigured) EmeraldSoft else NovaCoralSoft,
                ) {
                  Text(
                    text = if (uiState.ownerAdminConfig.isConfigured) "ACTIVE" else "OPTIONAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (uiState.ownerAdminConfig.isConfigured) EmeraldLive else NovaCoral,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }

              Text(
                text =
                  "Saved only on this phone (never inside customer APKs). Link your .myshopify.com domain and Admin token (write_products, read_orders) to publish products natively here.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate,
              )

              if (!uiState.ownerAdminConfig.isConfigured) {
                OutlinedTextField(
                  value = uiState.ownerAdminDomainInput,
                  onValueChange = onAdminDomainChanged,
                  label = { Text("Shopify Domain (e.g. mynovacart.myshopify.com)") },
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp),
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = PureWhite,
                      unfocusedContainerColor = PureWhite,
                    ),
                  modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                  value = uiState.ownerAdminTokenInput,
                  onValueChange = onAdminTokenChanged,
                  label = { Text("Admin API Access Token (shpat_...)") },
                  visualTransformation = PasswordVisualTransformation(),
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp),
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = PureWhite,
                      unfocusedContainerColor = PureWhite,
                    ),
                  modifier = Modifier.fillMaxWidth(),
                )
                Button(
                  onClick = onConnectAdminApi,
                  enabled = !uiState.isOwnerActionBusy,
                  modifier = Modifier.fillMaxWidth().height(46.dp),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
                ) {
                  Text("Connect Owner Admin API")
                }
              } else {
                HorizontalDivider(color = HairlineBorder)
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(Icons.Outlined.AddBox, contentDescription = null, tint = NovaCoral)
                  Text("Quick Add Product", style = MaterialTheme.typography.titleSmall, color = ObsidianInk)
                }

                OutlinedTextField(
                  value = productTitle,
                  onValueChange = { productTitle = it },
                  label = { Text("Product Title") },
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp),
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = PureWhite,
                      unfocusedContainerColor = PureWhite,
                    ),
                  modifier = Modifier.fillMaxWidth(),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { Text("Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors =
                      OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                      ),
                    modifier = Modifier.weight(1f),
                  )
                  OutlinedTextField(
                    value = productStock,
                    onValueChange = { productStock = it },
                    label = { Text("Stock Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors =
                      OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                      ),
                    modifier = Modifier.weight(1f),
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  uiState.categories.forEach { cat ->
                    FilterChip(
                      selected = selectedCategoryTitle == cat.title,
                      onClick = { selectedCategoryTitle = cat.title },
                      label = { Text(cat.title) },
                      colors =
                        FilterChipDefaults.filterChipColors(
                          selectedContainerColor = ObsidianInk,
                          selectedLabelColor = PureWhite,
                          containerColor = PureWhite,
                        ),
                    )
                  }
                }

                OutlinedTextField(
                  value = productDesc,
                  onValueChange = { productDesc = it },
                  label = { Text("Description") },
                  minLines = 2,
                  maxLines = 4,
                  shape = RoundedCornerShape(12.dp),
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = PureWhite,
                      unfocusedContainerColor = PureWhite,
                    ),
                  modifier = Modifier.fillMaxWidth(),
                )

                Button(
                  onClick = {
                    onCreateProduct(
                      productTitle,
                      productPrice,
                      productStock,
                      selectedCategoryTitle,
                      productDesc,
                    ) {
                      productTitle = ""
                      productPrice = ""
                      productDesc = ""
                    }
                  },
                  enabled = !uiState.isOwnerActionBusy,
                  modifier = Modifier.fillMaxWidth().height(48.dp),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
                ) {
                  Text("Publish Product to Shopify")
                }

                HorizontalDivider(color = HairlineBorder)

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = ObsidianInk)
                    Text(
                      "Recent Store Orders (${uiState.ownerAdminOrders.size})",
                      style = MaterialTheme.typography.titleSmall,
                      color = ObsidianInk,
                    )
                  }
                  IconButton(onClick = onRefreshOrders) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh orders")
                  }
                }

                uiState.ownerAdminOrders.take(8).forEach { order ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, HairlineBorder),
                    modifier = Modifier.fillMaxWidth(),
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Column {
                        Text(order.name, style = MaterialTheme.typography.titleSmall, color = ObsidianInk)
                        Text(
                          "${order.financialStatus ?: "pending"} • ${order.fulfillmentStatus ?: "unfulfilled"}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MutedSlate,
                        )
                      }
                      Text(
                        text = "₹${order.totalPrice}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLive,
                      )
                    }
                  }
                }

                OutlinedButton(
                  onClick = onDisconnectAdminApi,
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                ) {
                  Text("Disconnect Admin API from Device", color = ObsidianInk)
                }
              }
            }
          }
        }
      }
    }
  }
}
