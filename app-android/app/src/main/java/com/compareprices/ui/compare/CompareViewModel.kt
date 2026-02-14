package com.compareprices.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.AppDatabase
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.local.ShoppingListWithItems
import com.compareprices.data.local.seedDemoDataIfNeeded
import com.compareprices.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CompareViewModel @Inject constructor(
  private val database: AppDatabase,
  private val productDao: ProductDao,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao,
  private val priceSnapshotDao: PriceSnapshotDao,
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(CompareUiState())
  val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      seedDemoDataIfNeeded(database, shoppingListDao, productDao, listItemDao, priceSnapshotDao)
    }
    viewModelScope.launch {
      combine(
        shoppingListDao.observeLatestList(),
        userPreferencesRepository.remoteCompareEnabled
      ) { list, remoteEnabled ->
        CompareUiState(
          list = list,
          remoteCompareEnabled = remoteEnabled
        )
      }.collect { state ->
        _uiState.value = state
      }
    }
  }
}

data class CompareUiState(
  val list: ShoppingListWithItems? = null,
  val remoteCompareEnabled: Boolean = false
)
