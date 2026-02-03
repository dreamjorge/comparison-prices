package com.compareprices.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.PriceSnapshotEntity
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceHistoryViewModel @Inject constructor(
  private val productDao: ProductDao,
  private val priceSnapshotDao: PriceSnapshotDao,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val productId: Long = savedStateHandle.get<Long>("productId") ?: 0L
  
  private val _uiState = MutableStateFlow(PriceHistoryUiState())
  val uiState: StateFlow<PriceHistoryUiState> = _uiState.asStateFlow()

  init {
    loadHistory()
  }

  private fun loadHistory() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true)
      
      val product = productDao.getById(productId)
      val snapshots = priceSnapshotDao.getHistoryForProduct(productId, limit = 30)
      
      _uiState.value = PriceHistoryUiState(
        product = product,
        snapshots = snapshots,
        currentPrice = snapshots.firstOrNull()?.price,
        minPrice = snapshots.minOfOrNull { it.price },
        maxPrice = snapshots.maxOfOrNull { it.price },
        avgPrice = snapshots.map { it.price }.average().takeIf { !it.isNaN() },
        isLoading = false
      )
    }
  }
}

data class PriceHistoryUiState(
  val product: ProductEntity? = null,
  val snapshots: List<PriceSnapshotEntity> = emptyList(),
  val currentPrice: Double? = null,
  val minPrice: Double? = null,
  val maxPrice: Double? = null,
  val avgPrice: Double? = null,
  val isLoading: Boolean = false
)
