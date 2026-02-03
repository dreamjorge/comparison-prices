package com.compareprices.ui.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.repository.PremiumStatus
import com.compareprices.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

  val premiumUiState: StateFlow<PremiumUiState> = userPreferencesRepository.premiumStatus
    .map { status ->
      toPremiumUiState(status, System.currentTimeMillis())
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = PremiumUiState()
    )

  val isPremium: StateFlow<Boolean> = premiumUiState
    .map { it.hasAccess }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = false
    )

  init {
    viewModelScope.launch {
      userPreferencesRepository.clearExpiredRewards()
    }
  }

  fun purchasePremium() {
    viewModelScope.launch {
      // Mock purchase logic
      userPreferencesRepository.setPremium(true)
    }
  }

  fun unlockRewardedAccess(hours: Long = 24) {
    viewModelScope.launch {
      userPreferencesRepository.unlockRewardedAccess(durationMillis = hours * 60 * 60 * 1000L)
    }
  }

  fun resetPremium() {
    viewModelScope.launch {
      userPreferencesRepository.setPremium(false)
    }
  }
}

data class PremiumUiState(
  val isPaid: Boolean = false,
  val rewardedUntilMillis: Long = 0L,
  val hasAccess: Boolean = false,
  val rewardedActive: Boolean = false
)

private fun toPremiumUiState(status: PremiumStatus, nowMillis: Long): PremiumUiState = PremiumUiState(
  isPaid = status.isPaid,
  rewardedUntilMillis = status.rewardedUntilMillis,
  hasAccess = status.hasAccess(nowMillis),
  rewardedActive = status.isRewardedActive(nowMillis)
)
