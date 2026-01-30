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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compareprices.data.local.ListItemWithProduct
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CompareScreen(viewModel: CompareViewModel = hiltViewModel()) {
  val uiState by viewModel.uiState.collectAsState()
  val storePrices = remember { demoStorePrices() }
  val listItems = uiState.list?.items.orEmpty()
  val listStorePrices = filterStorePricesByList(storePrices, listItems)
  var query by rememberSaveable { mutableStateOf("") }

  val filteredStores = sortStorePricesByTotal(filterStorePrices(query, listStorePrices))
  val comparisons = buildStoreComparisons(filteredStores)
  val cheapestTotal = cheapestTotalValue(filteredStores)
  val listName = uiState.list?.list?.name ?: "Lista actual"
  val dateLabel = remember { formatTodayLabel() }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = listName,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = dateLabel,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
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

    items(comparisons) { comparison ->
      val store = comparison.store
      val isCheapest = cheapestTotal != null && storeTotalValue(store) == cheapestTotal
      StorePriceCard(
        store = store,
        isCheapest = isCheapest,
        savingsVsNext = comparison.savingsVsNext
      )
    }
  }
}

@Composable
private fun StorePriceCard(store: StorePrice, isCheapest: Boolean, savingsVsNext: Int?) {
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
          if (savingsVsNext != null && savingsVsNext > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Ahorro vs siguiente: ${formatCurrency(savingsVsNext)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
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
          text = formatCurrency(storeTotalValue(store)),
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
  val items: List<StoreItemPrice>
)

internal data class StoreItemPrice(
  val product: String,
  val price: String
)

internal data class StoreComparison(
  val store: StorePrice,
  val savingsVsNext: Int?
)

internal fun demoStorePrices(): List<StorePrice> {
  return listOf(
    StorePrice(
      storeName = "Super Norte",
      zone = "Recoleta",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.580"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.100"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 1.880")
      )
    ),
    StorePrice(
      storeName = "Walmart",
      zone = "Ciudad de México",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.450"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.020"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 1.920")
      )
    ),
    StorePrice(
      storeName = "Ahorro Max",
      zone = "Palermo",
      items = listOf(
        StoreItemPrice(product = "Leche entera", price = "$ 1.620"),
        StoreItemPrice(product = "Pan integral", price = "$ 1.090"),
        StoreItemPrice(product = "Arroz largo fino", price = "$ 2.120")
      )
    ),
    StorePrice(
      storeName = "Mercado Central",
      zone = "Belgrano",
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

internal fun filterStorePricesByList(
  storePrices: List<StorePrice>,
  listItems: List<ListItemWithProduct>
): List<StorePrice> {
  if (listItems.isEmpty()) {
    return storePrices
  }

  val wantedProducts = listItems
    .map { it.product.name.lowercase(Locale.getDefault()) }
    .toSet()

  return storePrices.mapNotNull { store ->
    val filteredItems = store.items.filter { item ->
      item.product.lowercase(Locale.getDefault()) in wantedProducts
    }
    if (filteredItems.isEmpty()) {
      null
    } else {
      store.copy(items = filteredItems)
    }
  }
}

internal fun sortStorePricesByTotal(storePrices: List<StorePrice>): List<StorePrice> {
  return storePrices.sortedBy { storeTotalValue(it) }
}

internal fun cheapestTotalValue(storePrices: List<StorePrice>): Int? {
  return storePrices.minOfOrNull { storeTotalValue(it) }
}

internal fun buildStoreComparisons(storePrices: List<StorePrice>): List<StoreComparison> {
  return storePrices.mapIndexed { index, store ->
    val nextStore = storePrices.getOrNull(index + 1)
    val savings = nextStore?.let {
      storeTotalValue(it) - storeTotalValue(store)
    }
    StoreComparison(store, savings)
  }
}

internal fun storeTotalValue(store: StorePrice): Int {
  return store.items.sumOf { parseTotalPrice(it.price) }
}

internal fun parseTotalPrice(totalLabel: String): Int {
  val digits = totalLabel.filter { it.isDigit() }
  return digits.toIntOrNull() ?: 0
}

internal fun formatCurrency(value: Int, locale: Locale = Locale.getDefault()): String {
  val formatter = NumberFormat.getCurrencyInstance(locale).apply {
    maximumFractionDigits = 0
    minimumFractionDigits = 0
  }
  return formatter.format(value)
}

internal fun formatTodayLabel(
  date: Date = Date(),
  locale: Locale = Locale.getDefault(),
  timeZone: TimeZone = TimeZone.getDefault()
): String {
  val formatter = SimpleDateFormat("d MMM yyyy", locale).apply {
    this.timeZone = timeZone
  }
  return formatter.format(date)
}
