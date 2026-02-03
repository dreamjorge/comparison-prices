package com.compareprices.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
          PriceHistoryChart(
            prices = uiState.snapshots.reversed().map { it.price },
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
private fun PriceHistoryChart(
  prices: List<Double>,
  modifier: Modifier = Modifier
) {
  val minPrice = prices.minOrNull() ?: return
  val maxPrice = prices.maxOrNull() ?: return
  val range = (maxPrice - minPrice).takeIf { it > 0 } ?: 1.0
  val lineColor = MaterialTheme.colorScheme.primary
  val guideColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

  Canvas(modifier = modifier) {
    if (prices.isEmpty()) return@Canvas

    val stepX = if (prices.size > 1) size.width / (prices.size - 1) else 0f
    val points = prices.mapIndexed { index, price ->
      val normalized = ((price - minPrice) / range).toFloat()
      Offset(x = stepX * index, y = size.height - (normalized * size.height))
    }

    drawLine(
      color = guideColor,
      start = Offset(0f, size.height),
      end = Offset(size.width, size.height),
      strokeWidth = 2f
    )

    val path = Path().apply {
      points.firstOrNull()?.let { moveTo(it.x, it.y) }
      points.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
      path = path,
      color = lineColor,
      style = Stroke(width = 4f, cap = StrokeCap.Round)
    )

    points.forEach { point ->
      drawCircle(
        color = lineColor,
        radius = 6f,
        center = point
      )
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
