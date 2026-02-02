package com.compareprices.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.AppDatabase
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ListItemEntity
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

  fun searchProducts(query: String) = productDao.searchByName("%$query%")

  fun addItemToList(productId: Long, quantity: Double, unit: String) {
    viewModelScope.launch {
      val listId = _uiState.value.list?.list?.id ?: return@launch
      val newItem = ListItemEntity(
        id = 0,
        listId = listId,
        productId = productId,
        quantity = quantity,
        unit = unit
      )
      listItemDao.insert(newItem)
    }
  }

  fun deleteItem(itemId: Long) {
    viewModelScope.launch {
      listItemDao.deleteById(itemId)
    }
  }

  fun updateItemQuantity(itemId: Long, delta: Double) {
    viewModelScope.launch {
      val currentItem = _uiState.value.list?.items?.find { it.item.id == itemId }
      if (currentItem != null) {
        val newQuantity = (currentItem.item.quantity + delta).coerceAtLeast(1.0)
        listItemDao.updateQuantity(itemId, newQuantity)
      }
    }
  }
}

data class HomeUiState(
  val list: ShoppingListWithItems? = null
)
