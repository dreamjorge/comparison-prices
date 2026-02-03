package com.compareprices.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compareprices.ui.components.AdBanner
import com.compareprices.data.local.ListItemWithProduct

@Composable
fun HomeScreen(
  onNavigateToHistory: (Long) -> Unit = {},
  viewModel: HomeViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val list = uiState.list
  var showAddDialog by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf<Long?>(null) }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = { showAddDialog = true }) {
        Icon(Icons.Default.Add, "Agregar producto")
      }
    }
  ) { padding ->
    if (list == null) {
      EmptyHomeState(modifier = Modifier.padding(padding))
    } else {
      HomeWithAds(
        padding = padding,
        list = list,
        onDelete = { showDeleteConfirm = it },
        onQuantityChange = { id, quantity -> 
          // HomeViewModel expects absolute quantity
          viewModel.updateItemQuantity(id, quantity)
        },
        onNavigateToHistory = onNavigateToHistory
      )
    }

    if (showAddDialog) {
      com.compareprices.ui.home.AddItemDialog(
        onDismiss = { showAddDialog = false },
        searchProducts = viewModel::searchProducts,
        onConfirm = { productId, quantity, unit ->
          viewModel.addItemToList(productId, quantity, unit)
        }
      )
    }

    showDeleteConfirm?.let { itemId ->
      AlertDialog(
        onDismissRequest = { showDeleteConfirm = null },
        title = { Text("Eliminar producto") },
        text = { Text("¿Seguro que quieres eliminar este producto de la lista?") },
        confirmButton = {
          TextButton(onClick = {
            viewModel.deleteItem(itemId)
            showDeleteConfirm = null
          }) {
            Text("Eliminar")
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirm = null }) {
            Text("Cancelar")
          }
        }
      )
    }
  }
}

@Composable
private fun HomeWithAds(
  padding: PaddingValues,
  list: com.compareprices.data.local.ShoppingListWithItems,
  onDelete: (Long) -> Unit,
  onQuantityChange: (Long, Double) -> Unit,
  onNavigateToHistory: (Long) -> Unit,
  premiumViewModel: com.compareprices.ui.premium.PremiumViewModel = hiltViewModel()
) {
  val isPremium by premiumViewModel.isPremium.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(padding)
  ) {
    LazyColumn(
      modifier = Modifier.weight(1f),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(text = list.list.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${list.items.size} productos en tu lista",
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }

      items(list.items) { listItem ->
        ListItemCard(
          listItem = listItem,
          onDelete = { onDelete(listItem.item.id) },
          onQuantityUpdate = { newQuantity ->
            onQuantityChange(listItem.item.id, newQuantity)
          },
          onClickProduct = { onNavigateToHistory(listItem.product.id) }
        )
      }
    }

    if (!isPremium) {
      AdBanner()
    }
  }
}

@Composable
private fun EmptyHomeState(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Tu lista esta vacia",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = "Agrega productos para comparar precios entre tiendas.",
      style = MaterialTheme.typography.bodyMedium
    )
  }
}

@Composable
private fun ListItemCard(
  listItem: ListItemWithProduct,
  onDelete: () -> Unit,
  onQuantityUpdate: (Double) -> Unit,
  onClickProduct: () -> Unit = {}
) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onClickProduct() }
        ) {
          Text(
            text = listItem.product.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          val brand = listItem.product.brand
          Text(
            text = if (brand.isNullOrBlank()) "Marca generica" else brand,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDelete) {
          Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(onClick = { onQuantityUpdate(listItem.item.quantity - 1.0) }) {
          Text("-", style = MaterialTheme.typography.titleLarge)
        }
        Text(
          text = "${listItem.item.quantity.toInt()} ${listItem.item.unit}",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
        TextButton(onClick = { onQuantityUpdate(listItem.item.quantity + 1.0) }) {
          Text("+", style = MaterialTheme.typography.titleLarge)
        }
      }
    }
  }
}
