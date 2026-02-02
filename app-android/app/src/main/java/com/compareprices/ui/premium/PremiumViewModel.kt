package com.compareprices.ui.premium

import androidx.lifecycle.ViewModel
<<<<<<< HEAD
import androidx.lifecycle.viewModelScope
import com.compareprices.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
=======
import com.compareprices.data.local.UserPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
>>>>>>> feature/develop-tickets
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
<<<<<<< HEAD
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

  val isPremium: StateFlow<Boolean> = userPreferencesRepository.isPremium
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = false
    )

  fun purchasePremium() {
    viewModelScope.launch {
      // Mock purchase logic
      userPreferencesRepository.setPremium(true)
    }
  }

  fun resetPremium() {
    viewModelScope.launch {
      userPreferencesRepository.setPremium(false)
    }
  }
}
=======
    val userPrefs: UserPrefs
) : ViewModel()
>>>>>>> feature/develop-tickets
