package com.example.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NotifyPreferencesDataStore
import com.example.data.NovaCartRepository
import com.example.model.FutureFeature
import com.example.model.LaunchCategory
import com.example.model.NotifyMeRegistration
import com.example.model.NovaCartCatalogBlueprint
import com.example.model.StoreLaunchStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovaCartUiState(
  val showLaunchSplash: Boolean = true,
  val categories: List<LaunchCategory> = NovaCartCatalogBlueprint.launchCategories,
  val futureFeatures: List<FutureFeature> = NovaCartCatalogBlueprint.futureShoppingFeatures,
  val registration: NotifyMeRegistration = NotifyMeRegistration(),
  val storeStatus: StoreLaunchStatus = StoreLaunchStatus(),
  val isNotifySheetVisible: Boolean = false,
  val notifyEmailInput: String = "",
  val notifyNameInput: String = "",
  val notifySelectedCategoryIds: Set<String> = emptySet(),
  val notifyEmailError: String? = null,
  val notifyJustSaved: Boolean = false,
  val selectedCategoryForPreview: LaunchCategory? = null,
  val inspectedLockedFeature: FutureFeature? = null,
)

class NovaCartViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = NovaCartRepository(NotifyPreferencesDataStore(application))

  private val _uiState = MutableStateFlow(NovaCartUiState())
  val uiState: StateFlow<NovaCartUiState> = _uiState.asStateFlow()

  init {
    observeRegistration()
    runLaunchSplashSequence()
    refreshStoreLaunchStatus()
  }

  private fun observeRegistration() {
    viewModelScope.launch {
      repository.registrationFlow.collect { saved ->
        _uiState.update { current ->
          current.copy(
            registration = saved,
            notifyEmailInput =
              if (current.isNotifySheetVisible) current.notifyEmailInput else saved.email,
            notifyNameInput =
              if (current.isNotifySheetVisible) current.notifyNameInput else saved.fullName,
            notifySelectedCategoryIds =
              if (current.isNotifySheetVisible) current.notifySelectedCategoryIds
              else if (saved.interestedCategoryIds.isNotEmpty()) saved.interestedCategoryIds
              else current.categories.map { it.id }.toSet(),
          )
        }
      }
    }
  }

  private fun runLaunchSplashSequence() {
    viewModelScope.launch {
      delay(1450L)
      _uiState.update { it.copy(showLaunchSplash = false) }
    }
  }

  fun dismissLaunchSplash() {
    _uiState.update { it.copy(showLaunchSplash = false) }
  }

  fun openNotifyMeSheet(preselectedCategoryId: String? = null) {
    _uiState.update { state ->
      val existing = state.registration
      val defaultCategories =
        when {
          preselectedCategoryId != null ->
            (existing.interestedCategoryIds + preselectedCategoryId).ifEmpty {
              setOf(preselectedCategoryId)
            }
          existing.interestedCategoryIds.isNotEmpty() -> existing.interestedCategoryIds
          else -> state.categories.map { it.id }.toSet()
        }
      state.copy(
        isNotifySheetVisible = true,
        notifyEmailInput = existing.email,
        notifyNameInput = existing.fullName,
        notifySelectedCategoryIds = defaultCategories,
        notifyEmailError = null,
        notifyJustSaved = false,
      )
    }
  }

  fun closeNotifyMeSheet() {
    _uiState.update { it.copy(isNotifySheetVisible = false, notifyEmailError = null) }
  }

  fun onNotifyEmailChanged(value: String) {
    _uiState.update { it.copy(notifyEmailInput = value, notifyEmailError = null) }
  }

  fun onNotifyNameChanged(value: String) {
    _uiState.update { it.copy(notifyNameInput = value) }
  }

  fun toggleNotifyCategorySelection(categoryId: String) {
    _uiState.update { state ->
      val updated = state.notifySelectedCategoryIds.toMutableSet()
      if (!updated.add(categoryId)) {
        updated.remove(categoryId)
      }
      state.copy(notifySelectedCategoryIds = updated)
    }
  }

  fun submitNotifyMe() {
    val state = _uiState.value
    val trimmedEmail = state.notifyEmailInput.trim()
    if (trimmedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
      _uiState.update { it.copy(notifyEmailError = "Please enter a valid email address.") }
      return
    }

    val chosenCategories =
      state.notifySelectedCategoryIds.ifEmpty { state.categories.map { it.id }.toSet() }

    viewModelScope.launch {
      repository.saveNotifyRegistration(
        email = trimmedEmail,
        fullName = state.notifyNameInput,
        categoryIds = chosenCategories,
      )
      _uiState.update { it.copy(notifyJustSaved = true, notifyEmailError = null) }
    }
  }

  fun unsubscribeLaunchAlert() {
    viewModelScope.launch {
      repository.unsubscribeLaunchAlert()
      _uiState.update { it.copy(notifyJustSaved = false, isNotifySheetVisible = false) }
    }
  }

  fun openCategoryPreview(category: LaunchCategory) {
    _uiState.update { it.copy(selectedCategoryForPreview = category) }
  }

  fun closeCategoryPreview() {
    _uiState.update { it.copy(selectedCategoryForPreview = null) }
  }

  fun toggleCategoryAlertFromPreview(categoryId: String) {
    viewModelScope.launch {
      val current = _uiState.value.registration
      if (!current.isSubscribed) {
        closeCategoryPreview()
        openNotifyMeSheet(preselectedCategoryId = categoryId)
      } else {
        repository.toggleCategoryInterest(categoryId)
      }
    }
  }

  fun inspectLockedFeature(feature: FutureFeature) {
    _uiState.update { it.copy(inspectedLockedFeature = feature) }
  }

  fun dismissLockedFeatureDialog() {
    _uiState.update { it.copy(inspectedLockedFeature = null) }
  }

  fun refreshStoreLaunchStatus() {
    viewModelScope.launch {
      _uiState.update { it.copy(storeStatus = it.storeStatus.copy(isChecking = true)) }
      val result = repository.checkStoreLaunchStatus()
      _uiState.update { it.copy(storeStatus = result) }
    }
  }
}
