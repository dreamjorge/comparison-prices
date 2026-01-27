package com.compareprices.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compareprices.data.local.ListItemDao
import com.compareprices.data.local.ListItemEntity
import com.compareprices.data.local.ProductDao
import com.compareprices.data.local.ProductEntity
import com.compareprices.data.local.ShoppingListDao
import com.compareprices.data.local.ShoppingListEntity
import com.compareprices.data.local.ShoppingListWithItems
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
  private val productDao: ProductDao,
  private val shoppingListDao: ShoppingListDao,
  private val listItemDao: ListItemDao
) : ViewModel() {
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      seedIfNeeded()
    }
    viewModelScope.launch {
      shoppingListDao.observeLatestList().collect { list ->
        _uiState.value = HomeUiState(list)
      }
    }
  }

  private suspend fun seedIfNeeded() {
    if (shoppingListDao.count() > 0) {
      return
    }

    val products = listOf(
      ProductEntity(name = "Leche entera", brand = "La Serenisima", defaultUnit = "litro"),
      ProductEntity(name = "Pan integral", brand = "Bimbo", defaultUnit = "unidad"),
      ProductEntity(name = "Arroz largo fino", brand = "Gallo", defaultUnit = "kg")
    )
    val productIds = productDao.insertAll(products)
    val listId = shoppingListDao.upsert(
      ShoppingListEntity(name = "Compra semanal", createdAt = System.currentTimeMillis())
    )

    val items = listOf(
      ListItemEntity(
        listId = listId,
        productId = productIds.getOrElse(0) { 0 },
        quantity = 2.0,
        unit = "L"
      ),
      ListItemEntity(
        listId = listId,
        productId = productIds.getOrElse(1) { 0 },
        quantity = 1.0,
        unit = "unidad"
      ),
      ListItemEntity(
        listId = listId,
        productId = productIds.getOrElse(2) { 0 },
        quantity = 1.0,
        unit = "kg"
      )
    )
    listItemDao.upsertAll(items)
  }
}

data class HomeUiState(
  val list: ShoppingListWithItems? = null
)
