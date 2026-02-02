package com.compareprices.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceHistoryScreen(
  productId: Long,
  onNavigateBack: () -> Unit,
  viewModel: PriceHistoryViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(uiState.product?.name ?: "Historial de Precios") },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
          }
        }
      )
    }
  ) { padding ->
    if (uiState.isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = androidx.compose.ui.Alignment.Center
      ) {
        CircularProgressIndicator()
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Stats cards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatCard(
          title = "Actual",
          value = uiState.currentPrice?.let { "$${String.format("%.2f", it)}" } ?: "N/A",
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "Mínimo",
          value = uiState.minPrice?.let { "$${String.format("%.2f", it)}" } ?: "N/A",
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "Máximo",
          value = uiState.maxPrice?.let { "$${String.format("%.2f", it)}" } ?: "N/A",
          modifier = Modifier.weight(1f)
        )
      }

      // Chart
      if (uiState.snapshots.isNotEmpty()) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
          val modelProducer = CartesianChartModelProducer.build()
          modelProducer.tryRunTransaction {
            lineSeries {
              series(
                uiState.snapshots.reversed().map { it.price }
              )
            }
          }

          CartesianChartHost(
            chart = rememberCartesianChart(
              rememberLineCartesianLayer(),
              startAxis = rememberStartAxis(),
              bottomAxis = rememberBottomAxis()
            ),
            modelProducer = modelProducer,
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp)
          )
        }
      } else {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
          ) {
            Text(
              text = "No hay datos de historial disponibles",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatCard(
  title: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Card(modifier = modifier) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
