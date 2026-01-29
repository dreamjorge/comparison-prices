package com.compareprices.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.AppDatabase
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
class HomeViewModel @Inject constructor(
  private val database: AppDatabase,
  private val productDao: ProductDao,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao
) : ViewModel() {
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      seedDemoDataIfNeeded(database, shoppingListDao, productDao, listItemDao)
    }
    viewModelScope.launch {
      shoppingListDao.observeLatestList().collect { list ->
        _uiState.value = HomeUiState(list)
      }
    }
  }

}

data class HomeUiState(
  val list: ShoppingListWithItems? = null
)
