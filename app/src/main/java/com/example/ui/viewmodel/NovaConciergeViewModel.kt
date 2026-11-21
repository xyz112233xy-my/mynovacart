package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "NovaConciergeVM"

sealed interface ConciergeResponseState {
  data object Idle : ConciergeResponseState
  data object Loading : ConciergeResponseState
  data class Streaming(val text: String) : ConciergeResponseState
  data class Success(val text: String) : ConciergeResponseState
  data class Error(val message: String) : ConciergeResponseState
}

data class ConciergeMessage(
  val id: Long,
  val isUser: Boolean,
  val text: String,
)

data class NovaConciergeUiState(
  val promptInput: String = "",
  val messages: List<ConciergeMessage> = emptyList(),
  val responseState: ConciergeResponseState = ConciergeResponseState.Idle,
)

class NovaConciergeViewModel : ViewModel() {

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      systemInstruction =
        content {
          text(
            """
            You are the NOVA CART Launch Concierge for https://mynovacart.in.
            NOVA CART is currently in Coming Soon pre-launch mode with five upcoming departments:
            Home Decor, Kitchen Accessories, Beauty Care, Daily Life Gadgets, and Kids.
            Help shoppers plan room styling, kitchen organization, self-care routines, daily gadget checklists, and kid-friendly gift ideas.
            Important rule: Never invent fake NOVA CART product SKUs, prices, stock counts, or discount offers because the online store is still launching. Keep answers concise, warm, and practical.
            """
              .trimIndent()
          )
        },
    )
  }

  private val _uiState = MutableStateFlow(NovaConciergeUiState())
  val uiState: StateFlow<NovaConciergeUiState> = _uiState.asStateFlow()

  private var activeJob: Job? = null

  fun onPromptChanged(value: String) {
    _uiState.update { it.copy(promptInput = value) }
  }

  fun sendQuickPrompt(prompt: String) {
    _uiState.update { it.copy(promptInput = prompt) }
    sendPrompt(prompt)
  }

  fun sendPrompt(rawPrompt: String = _uiState.value.promptInput) {
    val prompt = rawPrompt.trim()
    if (prompt.isEmpty()) return

    activeJob?.cancel()
    val userMessage =
      ConciergeMessage(id = System.nanoTime(), isUser = true, text = prompt)

    _uiState.update { state ->
      state.copy(
        promptInput = "",
        messages = state.messages + userMessage,
        responseState = ConciergeResponseState.Loading,
      )
    }

    activeJob =
      viewModelScope.launch {
        val accumulated = StringBuilder()
        try {
          withTimeout(60_000L) {
            model.generateContentStream(prompt).collect { chunk ->
              val chunkText = chunk.text.orEmpty()
              if (chunkText.isNotEmpty()) {
                accumulated.append(chunkText)
                _uiState.update {
                  it.copy(responseState = ConciergeResponseState.Streaming(accumulated.toString()))
                }
              }
            }
          }

          val finalText = accumulated.toString().trim()
          if (finalText.isNotEmpty()) {
            val reply =
              ConciergeMessage(id = System.nanoTime(), isUser = false, text = finalText)
            _uiState.update { state ->
              state.copy(
                messages = state.messages + reply,
                responseState = ConciergeResponseState.Success(finalText),
              )
            }
          } else {
            _uiState.update {
              it.copy(responseState = ConciergeResponseState.Error("Gemini returned an empty response."))
            }
          }
        } catch (e: TimeoutCancellationException) {
          Log.e(TAG, "Gemini generation timed out", e)
          _uiState.update {
            it.copy(
              responseState =
                ConciergeResponseState.Error(
                  "Request timed out after 60 seconds. Please try again."
                )
            )
          }
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          Log.e(TAG, "Firebase AI generation failed", e)
          _uiState.update {
            it.copy(
              responseState =
                ConciergeResponseState.Error(e.localizedMessage ?: "Failed to reach Gemini.")
            )
          }
        }
      }
  }

  fun clearConversation() {
    activeJob?.cancel()
    _uiState.value = NovaConciergeUiState()
  }
}
