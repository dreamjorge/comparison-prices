package com.compareprices.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.AppDatabase
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ListItemEntity
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.PriceSnapshotDao
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.local.ShoppingListWithItems
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
  private val listItemDao: ListItemDao,
  private val priceSnapshotDao: PriceSnapshotDao,
  private val userPrefs: com.compareprices.data.local.UserPrefs
) : ViewModel() {
  private val _uiState = MutableStateFlow(HomeUiState(isPro = userPrefs.isProUser))
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
<<<<<<< HEAD
=======
      seedDemoDataIfNeeded(database, shoppingListDao, productDao, listItemDao, priceSnapshotDao)
    }
    viewModelScope.launch {
>>>>>>> feature/develop-tickets
      shoppingListDao.observeLatestList().collect { list ->
        _uiState.value = HomeUiState(list)
      }
    }
  }

<<<<<<< HEAD
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

=======
>>>>>>> feature/develop-tickets
  fun deleteItem(itemId: Long) {
    viewModelScope.launch {
      listItemDao.deleteById(itemId)
    }
  }

<<<<<<< HEAD
  fun updateItemQuantity(itemId: Long, delta: Double) {
    viewModelScope.launch {
      val currentItem = _uiState.value.list?.items?.find { it.item.id == itemId }
      if (currentItem != null) {
        val newQuantity = (currentItem.item.quantity + delta).coerceAtLeast(1.0)
        listItemDao.updateQuantity(itemId, newQuantity)
=======
  fun updateItemQuantity(itemId: Long, quantity: Double) {
    viewModelScope.launch {
      if (quantity <= 0) {
        listItemDao.deleteById(itemId)
      } else {
        listItemDao.updateQuantity(itemId, quantity)
>>>>>>> feature/develop-tickets
      }
    }
  }
}

data class HomeUiState(
  val list: ShoppingListWithItems? = null,
  val isPro: Boolean = false
)
