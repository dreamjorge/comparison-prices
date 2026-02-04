package com.compareprices.ui.compare

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CompareScreen() {
  val storePrices = remember { demoStorePrices() }
  var query by rememberSaveable { mutableStateOf("") }

  val filteredStores = sortStorePricesByTotal(filterStorePrices(query, storePrices))
  val cheapestTotal = cheapestTotalValue(filteredStores)
  val savingsSummary = buildSavingsSummary(filteredStores)

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Buscar precios por supermercado",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          modifier = Modifier.fillMaxWidth(),
          label = { Text("Buscar tienda o producto") },
          singleLine = true
        )
      }
    }

    if (savingsSummary != null) {
      item {
        SavingsSummaryCard(summary = savingsSummary)
      }
    }

    if (filteredStores.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "No hay resultados",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Prueba con otra tienda o producto.",
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    }

    items(filteredStores) { store ->
      val isCheapest = cheapestTotal != null && parseTotalPrice(store.totalLabel) == cheapestTotal
      StorePriceCard(store = store, isCheapest = isCheapest)
    }
  }
}

@Composable
private fun SavingsSummaryCard(summary: SavingsSummary) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Mejor opción hoy",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSecondaryContainer
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = summary.storeName,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
          text = summary.totalLabel,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSecondaryContainer
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = summary.savingsLabel,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer
      )
    }
  }
}

@Composable
private fun StorePriceCard(store: StorePrice, isCheapest: Boolean) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = store.storeName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = store.zone,
            style = MaterialTheme.typography.bodySmall
          )
          if (isCheapest) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "Más barato",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
              )
            }
          }
        }
        Text(
          text = store.totalLabel,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      store.items.forEach { item ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = item.product, style = MaterialTheme.typography.bodyMedium)
          Text(text = item.price, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
  }
}

internal data class StorePrice(
  val storeName: String,
  val zone: String,
  val totalLabel: String,
  val items: List<StoreItemPrice>
)

internal data class StoreItemPrice(
  val product: String,
  val price: String
)

internal data class SavingsSummary(
  val storeName: String,
  val totalLabel: String,
  val savingsLabel: String
)

internal fun demoStorePrices(): List<StorePrice> {
  return listOf(
    StorePrice(
      storeName = "Super Norte",
      zone = "Recoleta",
      totalLabel = "$ 4.560",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.580"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.100"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 1.880")
      )
    ),
    StorePrice(
      storeName = "Walmart",
      zone = "Ciudad de México",
      totalLabel = "$ 4.390",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.450"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.020"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 1.920")
      )
    ),
    StorePrice(
      storeName = "Ahorro Max",
      zone = "Palermo",
      totalLabel = "$ 4.830",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.620"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.090"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 2.120")
      )
    ),
    StorePrice(
      storeName = "Mercado Central",
      zone = "Belgrano",
      totalLabel = "$ 4.420",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.470"),
        StoreItemPrice(product = "Pan integral", price = "$ 980"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 1.970")
      )
    )
  )
}

internal fun filterStorePrices(query: String, storePrices: List<StorePrice>): List<StorePrice> {
  val trimmedQuery = query.trim()
  if (trimmedQuery.isEmpty()) {
    return storePrices
  }

  return storePrices.filter { store ->
    val matchesStore = store.storeName.contains(trimmedQuery, ignoreCase = true)
    val matchesItem = store.items.any { it.product.contains(trimmedQuery, ignoreCase = true) }
    matchesStore || matchesItem
  }
}

internal fun sortStorePricesByTotal(storePrices: List<StorePrice>): List<StorePrice> {
  return storePrices.sortedBy { parseTotalPrice(it.totalLabel) }
}

internal fun cheapestTotalValue(storePrices: List<StorePrice>): Int? {
  return storePrices.minOfOrNull { parseTotalPrice(it.totalLabel) }
}

internal fun buildSavingsSummary(storePrices: List<StorePrice>): SavingsSummary? {
  if (storePrices.size < 2) {
    return null
  }

  val sortedStores = storePrices.sortedBy { parseTotalPrice(it.totalLabel) }
  val cheapest = sortedStores[0]
  val runnerUp = sortedStores[1]
  val cheapestTotal = parseTotalPrice(cheapest.totalLabel)
  val runnerUpTotal = parseTotalPrice(runnerUp.totalLabel)
  val savings = (runnerUpTotal - cheapestTotal).coerceAtLeast(0)
  val savingsLabel = if (savings > 0) {
    "Ahorra ${formatCurrency(savings)} vs ${runnerUp.storeName}"
  } else {
    "Empate con ${runnerUp.storeName}"
  }

  return SavingsSummary(
    storeName = cheapest.storeName,
    totalLabel = cheapest.totalLabel,
    savingsLabel = savingsLabel
  )
}

internal fun parseTotalPrice(totalLabel: String): Int {
  val digits = totalLabel.filter { it.isDigit() }
  return digits.toIntOrNull() ?: 0
}

internal fun formatCurrency(value: Int): String {
  val raw = value.coerceAtLeast(0).toString()
  val grouped = raw.reversed().chunked(3).joinToString(".").reversed()
  return "$ $grouped"
}
