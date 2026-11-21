package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.LaunchCategory
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.EmeraldLive
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NotifyMeBottomSheet(
  visible: Boolean,
  email: String,
  fullName: String,
  emailError: String?,
  categories: List<LaunchCategory>,
  selectedCategoryIds: Set<String>,
  isAlreadySubscribed: Boolean,
  justSaved: Boolean,
  onEmailChange: (String) -> Unit,
  onNameChange: (String) -> Unit,
  onToggleCategory: (String) -> Unit,
  onSubmit: () -> Unit,
  onUnsubscribe: () -> Unit,
  onDismiss: () -> Unit,
) {
  if (!visible) return

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    contentColor = ObsidianInk,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp)
          .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      if (justSaved) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = EmeraldSoft,
          border = BorderStroke(1.dp, EmeraldLive.copy(alpha = 0.2f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Launch alert saved",
                tint = EmeraldLive,
                modifier = Modifier.size(28.dp),
              )
              Text(
                text = "You are on the NOVA CART launch list!",
                style = MaterialTheme.typography.titleLarge,
                color = ObsidianInk,
              )
            }
            Text(
              text =
                "Your early-access alert for $email is saved on this device. When mynovacart.in opens, all selected departments will be ready to browse.",
              style = MaterialTheme.typography.bodyMedium,
              color = MutedSlate,
            )
          }
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth().height(52.dp).testTag("notify_done_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ObsidianInk, contentColor = PureWhite),
        ) {
          Text("Back to NOVA CART", style = MaterialTheme.typography.labelLarge)
        }
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = NovaCoralSoft,
            modifier = Modifier.size(46.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.NotificationsActive,
              contentDescription = "Launch notification",
              tint = NovaCoral,
              modifier = Modifier.padding(10.dp),
            )
          }
          Column {
            Text(
              text = "Get Early Launch Access",
              style = MaterialTheme.typography.headlineSmall,
              color = ObsidianInk,
            )
            Text(
              text = "We will notify you when mynovacart.in goes live.",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )
          }
        }

        OutlinedTextField(
          value = email,
          onValueChange = onEmailChange,
          label = { Text("Email address *") },
          placeholder = { Text("you@example.com") },
          isError = emailError != null,
          supportingText =
            if (emailError != null) {
              { Text(emailError, color = NovaCoral) }
            } else null,
          singleLine = true,
          keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
          shape = RoundedCornerShape(14.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ObsidianInk,
              unfocusedBorderColor = HairlineBorder,
              focusedContainerColor = AlabasterSurface,
              unfocusedContainerColor = AlabasterSurface,
            ),
          modifier = Modifier.fillMaxWidth().testTag("notify_email_input"),
        )

        OutlinedTextField(
          value = fullName,
          onValueChange = onNameChange,
          label = { Text("Your name (optional)") },
          placeholder = { Text("e.g. Aarav") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          shape = RoundedCornerShape(14.dp),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ObsidianInk,
              unfocusedBorderColor = HairlineBorder,
              focusedContainerColor = AlabasterSurface,
              unfocusedContainerColor = AlabasterSurface,
            ),
          modifier = Modifier.fillMaxWidth().testTag("notify_name_input"),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Categories you want to explore first",
            style = MaterialTheme.typography.titleSmall,
            color = ObsidianInk,
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            categories.forEach { category ->
              val selected = selectedCategoryIds.contains(category.id)
              FilterChip(
                selected = selected,
                onClick = { onToggleCategory(category.id) },
                label = { Text(category.title) },
                colors =
                  FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ObsidianInk,
                    selectedLabelColor = PureWhite,
                    containerColor = AlabasterSurface,
                    labelColor = ObsidianInk,
                  ),
                border =
                  FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = HairlineBorder,
                    selectedBorderColor = ObsidianInk,
                  ),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = onSubmit,
          modifier = Modifier.fillMaxWidth().height(54.dp).testTag("notify_submit_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NovaCoral, contentColor = PureWhite),
        ) {
          Icon(
            imageVector = Icons.Outlined.NotificationsActive,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isAlreadySubscribed) "Update Launch Alert" else "Notify Me at Launch",
            style = MaterialTheme.typography.labelLarge,
          )
        }

        if (isAlreadySubscribed) {
          TextButton(
            onClick = onUnsubscribe,
            modifier = Modifier.align(Alignment.CenterHorizontally).testTag("notify_remove_button"),
          ) {
            Text("Remove saved alert", color = MutedSlate)
          }
        }
      }
    }
  }
}
