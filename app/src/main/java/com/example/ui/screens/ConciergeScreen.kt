package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlabasterSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NovaCoral
import com.example.ui.theme.NovaCoralDark
import com.example.ui.theme.NovaCoralSoft
import com.example.ui.theme.ObsidianInk
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.ConciergeMessage
import com.example.ui.viewmodel.ConciergeResponseState
import com.example.ui.viewmodel.NovaConciergeUiState

private val starterPrompts =
  listOf(
    "Suggest warm minimalist Home Decor accent ideas",
    "Give me a smart Kitchen Accessories organization checklist",
    "Build a simple morning & night Beauty Care routine",
    "Practical Daily Life Gadgets and Kids gift ideas",
  )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConciergeScreen(
  uiState: NovaConciergeUiState,
  onPromptChanged: (String) -> Unit,
  onSendPrompt: () -> Unit,
  onQuickPromptClick: (String) -> Unit,
  onClearClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()
  val isBusy =
    uiState.responseState is ConciergeResponseState.Loading ||
      uiState.responseState is ConciergeResponseState.Streaming

  LaunchedEffect(uiState.messages.size, uiState.responseState) {
    if (uiState.messages.isNotEmpty()) {
      listState.animateScrollToItem(uiState.messages.lastIndex)
    }
  }

  Box(
    modifier = modifier.fillMaxSize().background(PureWhite).imePadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(modifier = Modifier.fillMaxSize().widthIn(max = 820.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = NovaCoralSoft,
            modifier = Modifier.size(40.dp),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = "NOVA Concierge",
                tint = NovaCoral,
                modifier = Modifier.size(20.dp),
              )
            }
          }
          Column {
            Text(
              text = "NOVA Concierge",
              style = MaterialTheme.typography.titleLarge,
              color = ObsidianInk,
            )
            Text(
              text = "Shopping & lifestyle assistant",
              style = MaterialTheme.typography.bodySmall,
              color = MutedSlate,
            )
          }
        }

        if (uiState.messages.isNotEmpty()) {
          IconButton(
            onClick = onClearClick,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("concierge_clear_button"),
          ) {
            Icon(
              imageVector = Icons.Outlined.DeleteOutline,
              contentDescription = "Clear conversation",
              tint = MutedSlate,
            )
          }
        }
      }

      // Messages / Empty State
      LazyColumn(
        state = listState,
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        if (uiState.messages.isEmpty() && uiState.responseState is ConciergeResponseState.Idle) {
          item {
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = AlabasterSurface),
              border = BorderStroke(1.dp, HairlineBorder),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Text(
                  text = "Your NOVA CART Shopping Assistant",
                  style = MaterialTheme.typography.titleMedium,
                  color = ObsidianInk,
                )
                Text(
                  text =
                    "Ask for styling inspiration, kitchen organization ideas, self-care routines, or gift checklists across our five departments connected to mynovacart.in.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MutedSlate,
                )
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  starterPrompts.forEachIndexed { idx, prompt ->
                    AssistChip(
                      onClick = { onQuickPromptClick(prompt) },
                      label = { Text(prompt, style = MaterialTheme.typography.bodySmall) },
                      colors =
                        AssistChipDefaults.assistChipColors(
                          containerColor = PureWhite,
                          labelColor = ObsidianInk,
                        ),
                      border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = HairlineBorder),
                      modifier = Modifier.testTag("concierge_chip_$idx"),
                    )
                  }
                }
              }
            }
          }
        }

        items(uiState.messages, key = { it.id }) { msg ->
          ConciergeBubble(message = msg)
        }

        when (val state = uiState.responseState) {
          is ConciergeResponseState.Loading -> {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = AlabasterSurface,
                border = BorderStroke(1.dp, HairlineBorder),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  CircularProgressIndicator(
                    color = NovaCoral,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp),
                  )
                  Text(
                    text = "NOVA Concierge is thinking...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate,
                  )
                }
              }
            }
          }

          is ConciergeResponseState.Streaming -> {
            item {
              ConciergeBubble(
                message = ConciergeMessage(id = -1L, isUser = false, text = state.text)
              )
            }
          }

          is ConciergeResponseState.Error -> {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = NovaCoralSoft,
                border = BorderStroke(1.dp, NovaCoral.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("concierge_error_banner"),
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Error",
                    tint = NovaCoralDark,
                  )
                  Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ObsidianInk,
                  )
                }
              }
            }
          }

          else -> Unit
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }
      }

      // Composer Row
      Surface(
        color = PureWhite,
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedTextField(
            value = uiState.promptInput,
            onValueChange = onPromptChanged,
            placeholder = { Text("Ask about decor, kitchen, beauty, gadgets, kids...") },
            singleLine = true,
            enabled = !isBusy,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSendPrompt() }),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ObsidianInk,
                unfocusedBorderColor = HairlineBorder,
                focusedContainerColor = AlabasterSurface,
                unfocusedContainerColor = AlabasterSurface,
              ),
            modifier = Modifier.weight(1f).testTag("concierge_prompt_input"),
          )

          IconButton(
            onClick = onSendPrompt,
            enabled = !isBusy && uiState.promptInput.isNotBlank(),
            colors =
              IconButtonDefaults.filledIconButtonColors(
                containerColor = NovaCoral,
                contentColor = PureWhite,
                disabledContainerColor = AlabasterSurface,
                disabledContentColor = MutedSlate,
              ),
            modifier =
              Modifier.size(48.dp)
                .minimumInteractiveComponentSize()
                .testTag("concierge_send_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send prompt",
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ConciergeBubble(message: ConciergeMessage) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
  ) {
    Surface(
      shape =
        RoundedCornerShape(
          topStart = 18.dp,
          topEnd = 18.dp,
          bottomStart = if (message.isUser) 18.dp else 4.dp,
          bottomEnd = if (message.isUser) 4.dp else 18.dp,
        ),
      color = if (message.isUser) ObsidianInk else AlabasterSurface,
      border = if (message.isUser) null else BorderStroke(1.dp, HairlineBorder),
      modifier = Modifier.widthIn(max = 560.dp),
    ) {
      Text(
        text = message.text,
        style = MaterialTheme.typography.bodyMedium,
        color = if (message.isUser) PureWhite else ObsidianInk,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      )
    }
  }
}
