package com.compareprices.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.local.ShoppingListWithItems
import com.compareprices.data.local.seedDemoDataIfNeeded
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CompareViewModel @Inject constructor(
  private val productDao: ProductDao,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao
) : ViewModel() {
  private val _uiState = MutableStateFlow(CompareUiState())
  val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      seedDemoDataIfNeeded(shoppingListDao, productDao, listItemDao)
    }
    viewModelScope.launch {
      shoppingListDao.observeLatestList().collect { list ->
        _uiState.value = CompareUiState(list)
      }
    }
  }
}

data class CompareUiState(
  val list: ShoppingListWithItems? = null
)
