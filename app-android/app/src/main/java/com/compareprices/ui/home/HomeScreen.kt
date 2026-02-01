package com.compareprices.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compareprices.data.local.ListItemWithProduct
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton

@Composable
fun HomeScreen(
  viewModel: HomeViewModel = hiltViewModel(),
  onNavigateToHistory: (Long, String) -> Unit = { _, _ -> }
) {
  val uiState by viewModel.uiState.collectAsState()
  val list = uiState.list

  if (list == null) {
    EmptyHomeState()
    return
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
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
        onDelete = { viewModel.deleteItem(listItem.item.id) },
        onUpdateQuantity = { delta -> 
          viewModel.updateItemQuantity(listItem.item.id, listItem.item.quantity + delta)
        },
        onClick = { onNavigateToHistory(listItem.product.id, listItem.product.name) }
      )
    }

    if (!uiState.isPro) {
      item {
        com.compareprices.ui.components.AdBanner(modifier = Modifier.padding(top = 8.dp))
      }
    }
  }
}

@Composable
private fun EmptyHomeState() {
  Column(
    modifier = Modifier
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
  onUpdateQuantity: (Double) -> Unit,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .androidx.compose.foundation.clickable(onClick = onClick)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = listItem.product.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          val brand = listItem.product.brand
          Text(
            text = if (brand.isNullOrBlank()) "Marca Genérica" else brand,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        androidx.compose.material3.IconButton(onClick = onDelete) {
          androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
            contentDescription = "Eliminar",
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
      
      Spacer(modifier = Modifier.height(8.dp))
      
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        androidx.compose.material3.TextButton(onClick = { onUpdateQuantity(-1.0) }) {
          Text("-")
        }
        Text(
          text = "${listItem.item.quantity.toInt()} ${listItem.item.unit}",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
        androidx.compose.material3.TextButton(onClick = { onUpdateQuantity(1.0) }) {
          Text("+")
        }
      }
    }
  }
}
