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

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
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
      ListItemCard(listItem)
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
private fun ListItemCard(listItem: ListItemWithProduct) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = listItem.product.name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val brand = listItem.product.brand
        Text(
          text = if (brand.isNullOrBlank()) "Marca generica" else brand,
          style = MaterialTheme.typography.bodySmall
        )
        Text(
          text = "${listItem.item.quantity} ${listItem.item.unit}",
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}
